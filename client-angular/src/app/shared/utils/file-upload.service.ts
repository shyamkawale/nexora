import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class FileUploadService {
  private uploadProgress$ = new Subject<number>();

  uploadFile(file: File, endpoint: string, httpService: any): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);

    const xhr = new XMLHttpRequest();

    xhr.upload.addEventListener('progress', (event) => {
      if (event.lengthComputable) {
        const percentComplete = (event.loaded / event.total) * 100;
        this.uploadProgress$.next(percentComplete);
      }
    });

    return httpService.postFormData(endpoint, formData);
  }

  getUploadProgress(): Observable<number> {
    return this.uploadProgress$.asObservable();
  }

  validateFileSize(file: File, maxSizeMB: number = 10): boolean {
    const maxBytes = maxSizeMB * 1024 * 1024;
    return file.size <= maxBytes;
  }

  validateFileType(file: File, allowedTypes: string[]): boolean {
    return allowedTypes.includes(file.type);
  }
}
