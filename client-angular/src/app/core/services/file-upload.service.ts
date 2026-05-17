import { Injectable } from '@angular/core';
import { HttpService } from './http.service';
import { Observable } from 'rxjs';

export interface PresignedUrlRequest {
  fileName: string;
  mimeType: string;
  fileSizeBytes: number;
}

export interface PresignedUrlResponse {
  fileKey: string;
  presignedUrl: string;
  expirationMinutes: number;
  fileUrl: string;
  mediaFilePublicId: string;
}

export interface ConfirmUploadRequest {
  mediaFilePublicId: string;
}

export interface MediaFileResponse {
  publicId: string;
  fileName: string;
  fileType: string;
  fileSize: number;
  fileKey: string;
  status: string;
  uploadedByPublicId: string;
  createdAt: string;
  confirmedAt: string;
}

export interface FileUploadProgress {
  fileName: string;
  fileSize: number;
  uploadedSize: number;
  percentage: number;
  status: 'pending' | 'uploading' | 'completed' | 'failed';
  error?: string;
}

export interface DownloadUrlResponse {
  presignedUrl: string;
  fileName: string;
}

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  constructor(private httpService: HttpService) { }

  /**
   * Request presigned URL from backend for S3 upload
   */
  getPresignedUrl(request: PresignedUrlRequest): Observable<PresignedUrlResponse> {
    console.log('📤 Requesting presigned URL for:', request.fileName);
    return this.httpService.post<PresignedUrlResponse>('/api/v1/files/presigned-upload-url', request);
  }

  /**
   * Get presigned URL from backend for S3 file (with optional download mode)
   * @param fileKey S3 file key
   * @param download if true, URL will force download; if false, allows inline viewing
   */
  getDownloadUrl(fileKey: string, download: boolean = false): Observable<DownloadUrlResponse> {
    console.log('📥 Requesting URL for file key:', fileKey, 'download mode:', download);
    return this.httpService.get<DownloadUrlResponse>(`/api/v1/files/presigned-download-url?fileKey=${encodeURIComponent(fileKey)}&download=${download}`);
  }

  /**
   * Confirm file upload after successfully uploading to S3
   */
  confirmUpload(request: ConfirmUploadRequest): Observable<MediaFileResponse> {
    console.log('✅ Confirming upload for mediaFilePublicId:', request.mediaFilePublicId);
    return this.httpService.post<MediaFileResponse>('/api/v1/files/confirm-upload', request);
  }

  /**
   * Upload file to S3 using presigned URL
   */
  uploadToS3(presignedUrl: string, file: File, onProgress?: (progress: number) => void): Observable<ProgressEvent> {
    return new Observable(observer => {
      const xhr = new XMLHttpRequest();

      // Track upload progress
      xhr.upload.addEventListener('progress', (event: ProgressEvent) => {
        if (event.lengthComputable) {
          const percentComplete = (event.loaded / event.total) * 100;
          console.log(`📊 Upload progress: ${percentComplete.toFixed(2)}%`);
          if (onProgress) {
            onProgress(percentComplete);
          }
        }
      });

      // Handle upload completion
      xhr.addEventListener('load', () => {
        if (xhr.status >= 200 && xhr.status < 300) {
          console.log('✅ File uploaded to S3 successfully');
          observer.next(new ProgressEvent('load'));
          observer.complete();
        } else {
          console.error('❌ S3 upload failed with status:', xhr.status);
          observer.error(new Error(`Upload failed with status ${xhr.status}`));
        }
      });

      // Handle upload errors
      xhr.addEventListener('error', () => {
        console.error('❌ S3 upload error:', xhr.statusText);
        observer.error(new Error('Upload failed: ' + xhr.statusText));
      });

      xhr.addEventListener('abort', () => {
        console.warn('⚠️ Upload cancelled');
        observer.error(new Error('Upload cancelled'));
      });

      // Prepare and send upload
      console.log('📤 Starting S3 upload...');
      xhr.open('PUT', presignedUrl, true);
      xhr.setRequestHeader('Content-Type', file.type);
      xhr.send(file);
    });
  }

  /**
   * Complete file upload workflow
   * 1. Get presigned URL from backend (creates PENDING MediaFile)
   * 2. Upload file to S3
   * 3. Confirm upload with backend (transitions MediaFile to CONFIRMED)
   * 4. Return mediaFilePublicId for attaching to messages
   */
  uploadFile(file: File, onProgress?: (progress: FileUploadProgress) => void): Observable<string> {
    return new Observable(observer => {
      const progressUpdate = (status: FileUploadProgress['status'], percentage = 0, error?: string) => {
        const progress: FileUploadProgress = {
          fileName: file.name,
          fileSize: file.size,
          uploadedSize: (percentage / 100) * file.size,
          percentage,
          status,
          error
        };
        if (onProgress) {
          onProgress(progress);
        }
      };

      // Step 1: Get presigned URL and create PENDING MediaFile
      progressUpdate('pending', 0);
      const request: PresignedUrlRequest = {
        fileName: file.name,
        mimeType: file.type,
        fileSizeBytes: file.size
      };

      this.getPresignedUrl(request).subscribe({
        next: (response) => {
          console.log('✅ Presigned URL received:', response.fileUrl);
          console.log('📋 MediaFile publicId:', response.mediaFilePublicId);

          // Step 2: Upload to S3
          progressUpdate('uploading', 5);
          this.uploadToS3(response.presignedUrl, file, (uploadPercentage) => {
            // Scale progress from 5% to 95% for upload
            const scaledProgress = 5 + (uploadPercentage * 0.9);
            progressUpdate('uploading', scaledProgress);
          }).subscribe({
            next: () => {
              console.log('✅ File uploaded to S3:', response.fileUrl);
              progressUpdate('uploading', 95);

              // Step 3: Confirm upload with backend
              const confirmRequest: ConfirmUploadRequest = {
                mediaFilePublicId: response.mediaFilePublicId
              };
              this.confirmUpload(confirmRequest).subscribe({
                next: (confirmResponse) => {
                  console.log('✅ Upload confirmed:', confirmResponse);
                  progressUpdate('uploading', 98);
                  
                  // Step 4: Fetch presigned download URL for displaying in chat
                  if (!confirmResponse.fileKey) {
                    observer.error(new Error('File key is missing from confirmation response'));
                    return;
                  }
                  
                  this.getDownloadUrl(confirmResponse.fileKey, false).subscribe({
                    next: (downloadResponse: any) => {
                      console.log('✅ Fetched presigned download URL:', downloadResponse.presignedUrl);
                      progressUpdate('completed', 100);
                      // Return the actual S3 presigned URL for displaying in chat
                      observer.next(downloadResponse.presignedUrl);
                      observer.complete();
                    },
                    error: (err: any) => {
                      console.error('❌ Failed to fetch presigned download URL:', err);
                      progressUpdate('failed', 0, 'Failed to fetch download URL');
                      observer.error(err);
                    }
                  });
                },
                error: (err) => {
                  console.error('❌ Failed to confirm upload:', err);
                  progressUpdate('failed', 0, 'Failed to confirm upload');
                  observer.error(err);
                }
              });
            },
            error: (err) => {
              console.error('❌ S3 upload failed:', err);
              progressUpdate('failed', 0, err.message);
              observer.error(err);
            }
          });
        },
        error: (err) => {
          console.error('❌ Failed to get presigned URL:', err);
          progressUpdate('failed', 0, err.message || 'Failed to get presigned URL');
          observer.error(err);
        }
      });
    });
  }

  /**
   * Validate file before upload
   */
  validateFile(file: File, maxSizeMb: number = 50, allowedTypes?: string[]): { valid: boolean; error?: string } {
    // Check file size
    const maxSizeBytes = maxSizeMb * 1024 * 1024;
    if (file.size > maxSizeBytes) {
      return {
        valid: false,
        error: `File size ${(file.size / (1024 * 1024)).toFixed(2)} MB exceeds maximum of ${maxSizeMb} MB`
      };
    }

    // Check file type if specified
    if (allowedTypes && allowedTypes.length > 0) {
      if (!allowedTypes.includes(file.type)) {
        return {
          valid: false,
          error: `File type ${file.type} is not allowed. Allowed types: ${allowedTypes.join(', ')}`
        };
      }
    }

    return { valid: true };
  }
}
