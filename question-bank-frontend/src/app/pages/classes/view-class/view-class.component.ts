import { Component, OnInit } from '@angular/core';
import { ClassEntity } from 'app/models/class-entities.model';
import { ClassEntityService } from 'app/services/class-entity.service';
import { AuthService } from 'app/services/auth.service';

@Component({
  selector: 'app-view-class',
  templateUrl: './view-class.component.html',
  styleUrls: ['./view-class.component.scss']
})
export class ViewClassComponent implements OnInit {

  classes: ClassEntity[] = [];
  editingClass: ClassEntity | null = null;
  isLoading: boolean = false;
  errorMessage: string = '';
  debugInfo: any = {};

  constructor(
    private classEntityService: ClassEntityService,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.performDiagnostics();
  }

  performDiagnostics() {
    this.checkAuthentication();
    this.getAllClasses();
  }

  checkAuthentication() {
    console.log('--- Authentication Check ---');

    const token = localStorage.getItem('token');
    const currentUser = this.authService.currentUserValue;
    const isLoggedIn = this.authService.isLoggedIn();

    this.debugInfo.auth = {
      hasToken: !!token,
      tokenLength: token ? token.length : 0,
      tokenPreview: token ? token.substring(0, 20) + '...' : 'No token',
      currentUser: currentUser,
      isLoggedIn: isLoggedIn
    };

    console.log('Authentication Info:', this.debugInfo.auth);

    if (!isLoggedIn) {
      this.errorMessage = 'Not authenticated. Please log in first.';
      console.error('User is not authenticated!');
    }
  }

  // REMOVED: testConnectivity() method - was causing 404 errors
  // The /actuator/health endpoint doesn't exist in your backend

  onClassUpdated() {
    this.getAllClasses();
    this.editingClass = null;
  }

  resetEditingMode() {
    this.editingClass = null;
  }

  editClass(classItem: ClassEntity) {

    if (!classItem.id) {
      console.error('Cannot edit class: ID is null or undefined', classItem);
      alert('Error: Class ID is missing. Cannot edit this class.');
      return;
    }

    this.editingClass = classItem;
  }

  deleteClass(classItem: ClassEntity) {
    console.log('Delete button clicked for class:', classItem);

    if (!classItem.id) {
      console.error('Cannot delete class: ID is null or undefined', classItem);
      alert('Error: Class ID is missing. Cannot delete this class.');
      return;
    }

    if (confirm(`Are you sure you want to delete "${classItem.name}"?`)) {
      this.classEntityService.deleteClassEntity(classItem.id).subscribe({
        next: () => {
          console.log('Class deleted successfully');
          this.getAllClasses();
        },
        error: (error) => {
          console.error('Error deleting class:', error);
          this.handleError(error, 'Failed to delete class');
        }
      });
    }
  }

  getAllClasses() {
    console.log('--- Fetching All Classes ---');
    this.isLoading = true;
    this.errorMessage = '';

    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        console.log('Raw API response:', data);

        data.forEach((classItem, index) => {
          console.log(`Class ${index}:`, {
            id: classItem.id,
            name: classItem.name,
            description: classItem.description,
            isActive: classItem.isActive,
            fullObject: classItem
          });
        });

        this.classes = data;
        this.isLoading = false;
        this.debugInfo.lastFetchTime = new Date().toISOString();
        this.debugInfo.classCount = data.length;
        this.debugInfo.connectivity = 'OK';
      },
      error: (error) => {
        console.error('Error fetching classes:', error);
        this.isLoading = false;
        this.debugInfo.connectivity = 'FAILED';
        this.handleError(error, 'Failed to fetch classes');
      }
    });
  }

  // Add retry logic
  getAllClassesWithRetry(retryCount: number = 0) {
    console.log(`--- Fetching All Classes (Attempt ${retryCount + 1}) ---`);
    this.isLoading = true;
    this.errorMessage = '';

    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        console.log('Raw API response:', data);
        this.classes = data;
        this.isLoading = false;
        this.debugInfo.lastFetchTime = new Date().toISOString();
        this.debugInfo.classCount = data.length;
        this.debugInfo.connectivity = 'OK';
      },
      error: (error) => {
        console.error(`Error fetching classes (attempt ${retryCount + 1}):`, error);
        this.isLoading = false;
        this.debugInfo.connectivity = 'FAILED';
        
        // Retry up to 2 times with increasing delay
        if (retryCount < 2 && error.status === 404) {
          console.log(`Retrying in ${(retryCount + 1) * 1000}ms...`);
          setTimeout(() => {
            this.getAllClassesWithRetry(retryCount + 1);
          }, (retryCount + 1) * 1000);
        } else {
          this.handleError(error, 'Failed to fetch classes');
        }
      }
    });
  }

  // This method now exists in your service
  tryActiveClasses() {
    console.log('--- Trying Active Classes Endpoint ---');
    this.isLoading = true;
    this.errorMessage = '';

    this.classEntityService.getAllActiveClasses().subscribe({
      next: (data) => {
        this.classes = data;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching active classes:', error);
        this.isLoading = false;
        this.handleError(error, 'Failed to fetch active classes');
      }
    });
  }

  // This method now exists in your service
  tryWithPagination() {
    this.isLoading = true;
    this.errorMessage = '';

    this.classEntityService.getAllClassEntitiesWithPagination(0, 10).subscribe({
      next: (data) => {
        this.classes = data.content; // Page object has content property
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error fetching paginated classes:', error);
        this.isLoading = false;
        this.handleError(error, 'Failed to fetch paginated classes');
      }
    });
  }

  private handleError(error: any, context: string) {
    this.debugInfo.lastError = {
      context: context,
      status: error.status,
      statusText: error.statusText,
      message: error.message,
      url: error.url,
      timestamp: new Date().toISOString()
    };

    if (error.status === 0) {
      this.errorMessage = 'Cannot connect to server. Check if backend is running.';
    } else if (error.status === 401) {
      this.errorMessage = 'Authentication failed. Please log in again.';
    } else if (error.status === 403) {
      this.errorMessage = 'Access denied. Check your permissions.';
    } else if (error.status === 404) {
      this.errorMessage = 'API endpoint not found. Check the URL configuration.';
    } else if (error.status >= 500) {
      this.errorMessage = 'Server error. Please try again later.';
    } else {
      this.errorMessage = `${context}: ${error.message || 'Unknown error'}`;
    }
  }

  refresh() {
    this.performDiagnostics();
  }

  clearDebugInfo() {
    this.debugInfo = {};
    this.errorMessage = '';
  }

  hasDebugInfo(): boolean {
    return this.debugInfo && Object.keys(this.debugInfo).length > 0;
  }

  getActiveCount(): number {
    return this.classes.filter(classItem => classItem.isActive).length;
  }

  getInactiveCount(): number {
    return this.classes.filter(classItem => !classItem.isActive).length;
  }
}