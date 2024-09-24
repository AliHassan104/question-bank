import { Routes } from '@angular/router';

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

export const AdminLayoutRoutes: Routes = [
    { path: 'dashboard',      component: DashboardComponent },
    { path: 'user',           component: UserComponent },
    { path: 'table',          component: TableComponent },
    { path: 'typography',     component: TypographyComponent },
    { path: 'icons',          component: IconsComponent },
    { path: 'maps',           component: MapsComponent },
    { path: 'notifications',  component: NotificationsComponent },
    { path: 'upgrade',        component: UpgradeComponent },

    // 

    { path: 'class',        component: ClassesComponent },
    { path: 'subject',        component: SubjectComponent },
    { path: 'chapter',        component: ChapterComponent },
    { path: 'question',        component: QuestionComponent },

];
