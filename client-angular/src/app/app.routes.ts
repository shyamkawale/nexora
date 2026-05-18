import { Routes } from '@angular/router';
import { AuthGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';
import { organizationContextGuard } from './core/guards/organization-context.guard';
import { HomeComponent } from './features/home/home.component';
import { ChatComponent } from './features/chat/chat.component';
import { PeopleComponent } from './features/people/people.component';
import { UserProfileComponent } from './features/user/user.component';
import { SettingsComponent } from './features/settings/settings.component';
import { FeedComponent } from './features/feed/feed.component';
import { OrganizationsComponent } from './features/organizations/organizations.component';
import { AdminDashboardComponent } from './features/admin/admin-dashboard/admin-dashboard.component';
import { UnauthorizedComponent } from './features/admin/unauthorized/unauthorized.component';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  {
    path: 'home',
    component: HomeComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'chat',
    component: ChatComponent,
    canActivate: [AuthGuard, organizationContextGuard]
  },
  {
    path: 'feed',
    component: FeedComponent,
    canActivate: [AuthGuard, organizationContextGuard]
  },
  {
    path: 'people',
    component: PeopleComponent,
    canActivate: [AuthGuard, organizationContextGuard]
  },
  {
    path: 'organizations',
    component: OrganizationsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'user',
    component: UserProfileComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'settings',
    component: SettingsComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'admin',
    children: [
      {
        path: 'dashboard',
        component: AdminDashboardComponent,
        canActivate: [AuthGuard, roleGuard],
        data: { roles: ['ADMIN'] }
      }
    ]
  },
  {
    path: 'unauthorized',
    component: UnauthorizedComponent
  },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];
