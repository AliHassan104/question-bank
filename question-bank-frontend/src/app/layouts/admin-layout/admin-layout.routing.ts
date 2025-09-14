import { Routes } from '@angular/router';
import { AuthGuard } from '../../guards/auth.guard';

import { DashboardComponent } from '../../pages/dashboard/dashboard.component';
import { UserComponent } from '../../pages/user/user.component';
import { TableComponent } from '../../pages/table/table.component';
import { TypographyComponent } from '../../pages/typography/typography.component';
import { IconsComponent } from '../../pages/icons/icons.component';
import { MapsComponent } from '../../pages/maps/maps.component';
import { NotificationsComponent } from '../../pages/notifications/notifications.component';
import { UpgradeComponent } from '../../pages/upgrade/upgrade.component';
import { ClassesComponent } from 'app/pages/classes/classes.component';
import { SubjectComponent } from 'app/pages/subject/subject.component';
import { ChapterComponent } from 'app/pages/chapter/chapter.component';
import { QuestionComponent } from 'app/pages/question/question.component';
import { GeneratePaperComponent } from 'app/pages/paper/generate-paper/generate-paper.component';

export const AdminLayoutRoutes: Routes = [
    { path: '', redirectTo: 'class', pathMatch: 'full' },
    { path: 'class', component: ClassesComponent, canActivate: [AuthGuard] },
    { path: 'subject', component: SubjectComponent, canActivate: [AuthGuard] },
    { path: 'subject/:id', component: GeneratePaperComponent, canActivate: [AuthGuard] },
    { path: 'chapter', component: ChapterComponent, canActivate: [AuthGuard] },
    { path: 'question', component: QuestionComponent, canActivate: [AuthGuard] },
    { path: 'generate-paper', component: GeneratePaperComponent, canActivate: [AuthGuard] },
];