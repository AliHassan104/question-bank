import { BrowserAnimationsModule } from "@angular/platform-browser/animations";
import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { ToastrModule } from "ngx-toastr";

import { SidebarModule } from './sidebar/sidebar.module';
import { FooterModule } from './shared/footer/footer.module';
import { NavbarModule } from './shared/navbar/navbar.module';
import { FixedPluginModule } from './shared/fixedplugin/fixedplugin.module';

import { AppComponent } from './app.component';
import { AppRoutes } from './app.routing';

import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';
import { ClassesComponent } from './pages/classes/classes.component';
import { AddClassComponent } from './pages/classes/add-class/add-class.component';
import { ViewClassComponent } from './pages/classes/view-class/view-class.component';
import { SubjectComponent } from './pages/subject/subject.component';
import { ViewSubjectComponent } from './pages/subject/view-subject/view-subject.component';
import { AddSubjectComponent } from './pages/subject/add-subject/add-subject.component';
import { ChapterComponent } from './pages/chapter/chapter.component';
import { AddChapterComponent } from './pages/chapter/add-chapter/add-chapter.component';
import { ViewChapterComponent } from './pages/chapter/view-chapter/view-chapter.component';
import { QuestionComponent } from './pages/question/question.component';
import { ViewQuestionComponent } from './pages/question/view-question/view-question.component';
import { AddQuestionComponent } from './pages/question/add-question/add-question.component';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from '@angular/common/http';
import { GeneratePaperComponent } from './pages/paper/generate-paper/generate-paper.component';
import { HTTP_INTERCEPTORS } from '@angular/common/http';
import { AuthInterceptor } from './interceptors/auth.interceptor';
import { LoginComponent } from './pages/login/login.component';

@NgModule({
  declarations: [
    AppComponent,
    AdminLayoutComponent,
    ClassesComponent,
    AddClassComponent,
    ViewClassComponent,
    SubjectComponent,
    ViewSubjectComponent,
    AddSubjectComponent,
    ChapterComponent,
    AddChapterComponent,
    ViewChapterComponent,
    QuestionComponent,
    ViewQuestionComponent,
    AddQuestionComponent,
    GeneratePaperComponent,
    LoginComponent,

  ],
  imports: [
    BrowserAnimationsModule,
    ReactiveFormsModule,
    RouterModule.forRoot(AppRoutes, {
      useHash: true
    }),
    SidebarModule,
    NavbarModule,
    ToastrModule.forRoot(),
    FooterModule,
    FixedPluginModule,
    HttpClientModule,
    FormsModule 

  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
