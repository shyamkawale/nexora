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
    return this.httpService.post<PresignedUrlResponse>('/api/v1/files/presigned-url', request);
  }

  /**
   * Get presigned URL from backend for S3 file (with optional download mode)
   * @param fileKey S3 file key
   * @param download if true, URL will force download; if false, allows inline viewing
   */
  getDownloadUrl(fileKey: string, download: boolean = false): Observable<DownloadUrlResponse> {
    console.log('📥 Requesting URL for file key:', fileKey, 'download mode:', download);
    return this.httpService.get<DownloadUrlResponse>(`/api/v1/files/download-url?fileKey=${encodeURIComponent(fileKey)}&download=${download}`);
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
   * 1. Get presigned URL from backend
   * 2. Upload file to S3
   * 3. Return file URL
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

      // Step 1: Get presigned URL
      progressUpdate('pending', 0);
      const request: PresignedUrlRequest = {
        fileName: file.name,
        mimeType: file.type,
        fileSizeBytes: file.size
      };

      this.getPresignedUrl(request).subscribe({
        next: (response) => {
          console.log('✅ Presigned URL received:', response.fileUrl);

          // Step 2: Upload to S3
          progressUpdate('uploading', 5);
          this.uploadToS3(response.presignedUrl, file, (uploadPercentage) => {
            // Scale progress from 5% to 95% for upload
            const scaledProgress = 5 + (uploadPercentage * 0.9);
            progressUpdate('uploading', scaledProgress);
          }).subscribe({
            next: () => {
              console.log('✅ File upload complete:', response.fileUrl);
              progressUpdate('completed', 100);
              // Return the file URL for storing in database
              observer.next(response.fileUrl);
              observer.complete();
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
