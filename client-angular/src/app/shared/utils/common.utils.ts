export const FLEXBOX_UTILITIES = {
  FRCC: 'display: flex; flex-direction: row; justify-content: center; align-items: center;',
  FRCB: 'display: flex; flex-direction: row; justify-content: space-between; align-items: center;',
  FRCS: 'display: flex; flex-direction: row; justify-content: center; align-items: flex-start;',
  FRSE: 'display: flex; flex-direction: row; justify-content: flex-start; align-items: flex-end;',
  FRSS: 'display: flex; flex-direction: row; justify-content: flex-start; align-items: flex-start;',
  FRCJE: 'display: flex; flex-direction: row; justify-content: center; align-items: flex-end;',
  FRCCE: 'display: flex; flex-direction: row; justify-content: center; align-items: center;',
  FCCC: 'display: flex; flex-direction: column; justify-content: center; align-items: center;',
  FCCB: 'display: flex; flex-direction: column; justify-content: space-between; align-items: center;',
};

export class CommonUtils {
  static formatDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleDateString('en-US', {
      year: 'numeric',
      month: 'short',
      day: 'numeric'
    });
  }

  static formatTime(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    return d.toLocaleTimeString('en-US', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  static formatDateTime(date: Date | string): string {
    return `${this.formatDate(date)} ${this.formatTime(date)}`;
  }

  static getFileCategory(filename: string): string {
    const ext = filename.split('.').pop()?.toLowerCase() || '';
    const imageExts = ['jpg', 'jpeg', 'png', 'gif', 'webp', 'svg'];
    const videoExts = ['mp4', 'avi', 'mov', 'mkv', 'flv'];
    const audioExts = ['mp3', 'wav', 'aac', 'flac'];
    const docExts = ['pdf', 'doc', 'docx', 'xlsx', 'xls', 'ppt', 'pptx', 'txt'];

    if (imageExts.includes(ext)) return 'image';
    if (videoExts.includes(ext)) return 'video';
    if (audioExts.includes(ext)) return 'audio';
    if (docExts.includes(ext)) return 'document';
    return 'file';
  }

  static truncateString(str: string, length: number): string {
    return str.length > length ? str.substring(0, length) + '...' : str;
  }

  static debounce(func: Function, delay: number): (...args: any[]) => void {
    let timeoutId: any;
    return (...args: any[]) => {
      clearTimeout(timeoutId);
      timeoutId = setTimeout(() => func(...args), delay);
    };
  }

  static throttle(func: Function, delay: number): (...args: any[]) => void {
    let lastCall = 0;
    return (...args: any[]) => {
      const now = Date.now();
      if (now - lastCall >= delay) {
        func(...args);
        lastCall = now;
      }
    };
  }

  static getServerDomain(): string {
    return 'http://localhost:8080'; // Configure this based on environment
  }
}
