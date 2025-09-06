package com.example.questionbank.config;

import com.example.questionbank.model.*;
import com.example.questionbank.model.enums.DifficultyLevel;
import com.example.questionbank.model.enums.QuestionType;
import com.example.questionbank.model.enums.SectionType;
import com.example.questionbank.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class CorrectedDatabaseInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ClassRepository classRepository;
    private final SubjectRepository subjectRepository;
    private final ChapterRepository chapterRepository;
    private final QuestionRepository questionRepository;
    private final MCQOptionRepository mcqOptionRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("Starting corrected database initialization...");

        // Only initialize if database is empty
        if (userRepository.count() > 0) {
            log.info("Database already contains data. Skipping initialization.");
            return;
        }

        try {
            initializePermissions();
            initializeRoles();
            initializeUsers();
            initializeClasses();
            initializeSubjects();
            initializeChapters();
            initializeQuestions();

            log.info("Database initialization completed successfully!");
        } catch (Exception e) {
            log.error("Error during database initialization: ", e);
            throw e;
        }
    }

    private void initializePermissions() {
        log.info("Initializing permissions...");

        String[] permissionNames = {
                "USER_READ", "USER_WRITE", "USER_DELETE",
                "QUESTION_READ", "QUESTION_WRITE", "QUESTION_DELETE",
                "SUBJECT_READ", "SUBJECT_WRITE", "SUBJECT_DELETE",
                "CHAPTER_READ", "CHAPTER_WRITE", "CHAPTER_DELETE"
        };

        for (String name : permissionNames) {
            Permission permission = Permission.builder()
                    .name(name)
                    .value(true)
                    .build();
            permissionRepository.save(permission);
        }
    }

    private void initializeRoles() {
        log.info("Initializing roles...");

        // Get all permissions
        Set<Permission> allPermissions = Set.copyOf(permissionRepository.findAll());

        // Create ADMIN role with all permissions
        Role adminRole = Role.builder()
                .name("ADMIN")
                .permissions(allPermissions)
                .build();
        roleRepository.save(adminRole);

        // Create TEACHER role with limited permissions
        Set<Permission> teacherPermissions = Set.of(
                findPermissionByName("USER_READ"),
                findPermissionByName("QUESTION_READ"),
                findPermissionByName("QUESTION_WRITE"),
                findPermissionByName("QUESTION_DELETE"),
                findPermissionByName("SUBJECT_READ"),
                findPermissionByName("SUBJECT_WRITE"),
                findPermissionByName("CHAPTER_READ"),
                findPermissionByName("CHAPTER_WRITE"),
                findPermissionByName("CHAPTER_DELETE")
        );

        Role teacherRole = Role.builder()
                .name("TEACHER")
                .permissions(teacherPermissions)
                .build();
        roleRepository.save(teacherRole);

        // Create STUDENT role with read-only permissions
        Set<Permission> studentPermissions = Set.of(
                findPermissionByName("USER_READ"),
                findPermissionByName("QUESTION_READ"),
                findPermissionByName("SUBJECT_READ"),
                findPermissionByName("CHAPTER_READ")
        );

        Role studentRole = Role.builder()
                .name("STUDENT")
                .permissions(studentPermissions)
                .build();
        roleRepository.save(studentRole);
    }

    private Permission findPermissionByName(String name) {
        return permissionRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Permission not found: " + name));
    }

    private void initializeUsers() {
        log.info("Initializing users...");

        // Create Admin User
        User admin = User.builder()
                .name("admin")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(findRoleByName("ADMIN")))
                .build();
        userRepository.save(admin);

        // Create Teacher User
        User teacher = User.builder()
                .name("teacher1")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(findRoleByName("TEACHER")))
                .build();
        userRepository.save(teacher);

        // Create Student User
        User student = User.builder()
                .name("student1")
                .password(passwordEncoder.encode("password123"))
                .roles(Set.of(findRoleByName("STUDENT")))
                .build();
        userRepository.save(student);
    }

    private Role findRoleByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Role not found: " + name));
    }

    private void initializeClasses() {
        log.info("Initializing classes...");

        ClassEntity class10 = ClassEntity.builder()
                .name("Class 10")
                .description("Tenth Grade")
                .isActive(true)
                .build();
        classRepository.save(class10);

        ClassEntity class11 = ClassEntity.builder()
                .name("Class 11")
                .description("Eleventh Grade")
                .isActive(true)
                .build();
        classRepository.save(class11);

        ClassEntity class12 = ClassEntity.builder()
                .name("Class 12")
                .description("Twelfth Grade")
                .isActive(true)
                .build();
        classRepository.save(class12);
    }

    private void initializeSubjects() {
        log.info("Initializing subjects...");

        ClassEntity class10 = findClassByName("Class 10");
        ClassEntity class11 = findClassByName("Class 11");
        ClassEntity class12 = findClassByName("Class 12");

        Subject mathematics = Subject.builder()
                .name("Mathematics")
                .description("Mathematics for Class 10")
                .classEntity(class10)
                .isActive(true)
                .build();
        subjectRepository.save(mathematics);

        Subject physics = Subject.builder()
                .name("Physics")
                .description("Physics for Class 10")
                .classEntity(class10)
                .isActive(true)
                .build();
        subjectRepository.save(physics);

        Subject chemistry = Subject.builder()
                .name("Chemistry")
                .description("Chemistry for Class 11")
                .classEntity(class11)
                .isActive(true)
                .build();
        subjectRepository.save(chemistry);

        Subject biology = Subject.builder()
                .name("Biology")
                .description("Biology for Class 12")
                .classEntity(class12)
                .isActive(true)
                .build();
        subjectRepository.save(biology);
    }

    private ClassEntity findClassByName(String name) {
        return classRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Class not found: " + name));
    }

    private void initializeChapters() {
        log.info("Initializing chapters...");

        Subject mathematics = findSubjectByName("Mathematics");
        Subject physics = findSubjectByName("Physics");
        Subject chemistry = findSubjectByName("Chemistry");

        Chapter algebra = Chapter.builder()
                .name("Algebra")
                .description("Basic Algebra concepts")
                .subject(mathematics)
                .isActive(true)
                .build();
        chapterRepository.save(algebra);

        Chapter geometry = Chapter.builder()
                .name("Geometry")
                .description("Geometric shapes and properties")
                .subject(mathematics)
                .isActive(true)
                .build();
        chapterRepository.save(geometry);

        Chapter motion = Chapter.builder()
                .name("Motion")
                .description("Laws of motion")
                .subject(physics)
                .isActive(true)
                .build();
        chapterRepository.save(motion);

        Chapter organicChem = Chapter.builder()
                .name("Organic Chemistry")
                .description("Carbon compounds")
                .subject(chemistry)
                .isActive(true)
                .build();
        chapterRepository.save(organicChem);
    }

    private Subject findSubjectByName(String name) {
        return subjectRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Subject not found: " + name));
    }

    private void initializeQuestions() {
        log.info("Initializing questions...");

        Chapter algebra = findChapterByName("Algebra");
        Chapter geometry = findChapterByName("Geometry");
        Chapter motion = findChapterByName("Motion");
        Chapter organicChem = findChapterByName("Organic Chemistry");

        // Algebra MCQ Question
        Question algebraQuestion = Question.builder()
                .questionText("What is the value of x in the equation 2x + 5 = 15?")
                .explanation("Solve by isolating x: 2x = 15 - 5 = 10, so x = 5")
                .sectionType(SectionType.MCQ)
                .questionType(QuestionType.SINGLE_CHOICE)
                .difficultyLevel(DifficultyLevel.EASY)
                .marks(1.0)
                .negativeMarks(0.0)
                .chapter(algebra)
                .isActive(true)
                .isAddedToPaper(false)
                .build();
        Question savedAlgebraQ = questionRepository.save(algebraQuestion);

        // Add MCQ options for algebra question
        createMCQOption(savedAlgebraQ, "5", true, 1);
        createMCQOption(savedAlgebraQ, "3", false, 2);
        createMCQOption(savedAlgebraQ, "7", false, 3);
        createMCQOption(savedAlgebraQ, "10", false, 4);

        // Geometry MCQ Question
        Question geometryQuestion = Question.builder()
                .questionText("Calculate the area of a circle with radius 7 cm.")
                .explanation("Use formula A = πr². A = π × 7² = 49π cm²")
                .sectionType(SectionType.MCQ)
                .questionType(QuestionType.SINGLE_CHOICE)
                .difficultyLevel(DifficultyLevel.MEDIUM)
                .marks(2.0)
                .negativeMarks(0.5)
                .chapter(geometry)
                .isActive(true)
                .isAddedToPaper(false)
                .build();
        Question savedGeometryQ = questionRepository.save(geometryQuestion);

        // Add MCQ options for geometry question
        createMCQOption(savedGeometryQ, "49π cm²", true, 1);
        createMCQOption(savedGeometryQ, "14π cm²", false, 2);
        createMCQOption(savedGeometryQ, "49 cm²", false, 3);
        createMCQOption(savedGeometryQ, "98π cm²", false, 4);

        // Physics Short Question (Use SHORT_ANSWER instead of DESCRIPTIVE)
        Question physicsQuestion = Question.builder()
                .questionText("State Newton's First Law of Motion.")
                .explanation("An object at rest stays at rest and an object in motion stays in motion unless acted upon by an external force.")
                .sectionType(SectionType.SHORT_QUESTION)
                .questionType(QuestionType.SHORT_ANSWER)  // Changed from DESCRIPTIVE
                .difficultyLevel(DifficultyLevel.EASY)
                .marks(3.0)
                .negativeMarks(0.0)
                .chapter(motion)
                .isActive(true)
                .isAddedToPaper(false)
                .build();
        questionRepository.save(physicsQuestion);

        // Chemistry MCQ Question
        Question chemQuestion = Question.builder()
                .questionText("What is the molecular formula of methane?")
                .explanation("CH₄ - One carbon atom bonded to four hydrogen atoms")
                .sectionType(SectionType.MCQ)
                .questionType(QuestionType.SINGLE_CHOICE)
                .difficultyLevel(DifficultyLevel.EASY)
                .marks(1.0)
                .negativeMarks(0.25)
                .chapter(organicChem)
                .isActive(true)
                .isAddedToPaper(false)
                .build();
        Question savedChemQ = questionRepository.save(chemQuestion);

        // Add MCQ options for chemistry question
        createMCQOption(savedChemQ, "CH₄", true, 1);
        createMCQOption(savedChemQ, "C₂H₆", false, 2);
        createMCQOption(savedChemQ, "CH₃OH", false, 3);
        createMCQOption(savedChemQ, "C₆H₆", false, 4);
    }

    private Chapter findChapterByName(String name) {
        return chapterRepository.findByName(name)
                .orElseThrow(() -> new RuntimeException("Chapter not found: " + name));
    }

    private void createMCQOption(Question question, String optionText, boolean isCorrect, int order) {
        MCQOption option = MCQOption.builder()
                .optionText(optionText)
                .isCorrect(isCorrect)
                .optionOrder(order)
                .question(question)
                .build();
        mcqOptionRepository.save(option);
    }
}