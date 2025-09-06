-- Corrected data.sql - Based on actual entity structure

-- Insert Permissions (assuming Permission entity exists)
INSERT INTO permissions (id, name, value, created_at, updated_at, created_by, updated_by) VALUES
(1, 'USER_READ', true, NOW(), NOW(), 'system', 'system'),
(2, 'USER_WRITE', true, NOW(), NOW(), 'system', 'system'),
(3, 'USER_DELETE', true, NOW(), NOW(), 'system', 'system'),
(4, 'QUESTION_READ', true, NOW(), NOW(), 'system', 'system'),
(5, 'QUESTION_WRITE', true, NOW(), NOW(), 'system', 'system'),
(6, 'QUESTION_DELETE', true, NOW(), NOW(), 'system', 'system'),
(7, 'SUBJECT_READ', true, NOW(), NOW(), 'system', 'system'),
(8, 'SUBJECT_WRITE', true, NOW(), NOW(), 'system', 'system'),
(9, 'SUBJECT_DELETE', true, NOW(), NOW(), 'system', 'system'),
(10, 'CHAPTER_READ', true, NOW(), NOW(), 'system', 'system'),
(11, 'CHAPTER_WRITE', true, NOW(), NOW(), 'system', 'system'),
(12, 'CHAPTER_DELETE', true, NOW(), NOW(), 'system', 'system');

-- Insert Roles
INSERT INTO roles (id, name, created_at, updated_at, created_by, updated_by) VALUES
(1, 'ADMIN', NOW(), NOW(), 'system', 'system'),
(2, 'TEACHER', NOW(), NOW(), 'system', 'system'),
(3, 'STUDENT', NOW(), NOW(), 'system', 'system');

-- Insert Role-Permission mappings
-- ADMIN has all permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6),
(1, 7), (1, 8), (1, 9), (1, 10), (1, 11), (1, 12);

-- TEACHER has read/write permissions (no delete for users)
INSERT INTO role_permissions (role_id, permission_id) VALUES
(2, 1), (2, 4), (2, 5), (2, 6), (2, 7), (2, 8),
(2, 10), (2, 11), (2, 12);

-- STUDENT has only read permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
(3, 1), (3, 4), (3, 7), (3, 10);

-- Insert Users (passwords are BCrypt encoded for "password123")
INSERT INTO users (id, name, password) VALUES
(1, 'admin', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
(2, 'teacher1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a'),
(3, 'student1', '$2a$10$8.UnVuG9HHgffUDAlk8qfOuVGkqRzgVymGe07xd00DMxs.AQubh4a');

-- Insert User-Role mappings
INSERT INTO user_roles (user_id, role_id) VALUES
(1, 1), -- admin has ADMIN role
(2, 2), -- teacher1 has TEACHER role
(3, 3); -- student1 has STUDENT role

-- Insert sample Classes (based on ClassEntity structure)
INSERT INTO classes (id, name, description, is_active, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Class 10', 'Tenth Grade', true, NOW(), NOW(), 'admin', 'admin'),
(2, 'Class 11', 'Eleventh Grade', true, NOW(), NOW(), 'admin', 'admin'),
(3, 'Class 12', 'Twelfth Grade', true, NOW(), NOW(), 'admin', 'admin');

-- Insert sample Subjects (based on Subject entity structure)
INSERT INTO subjects (id, name, description, class_id, is_active, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Mathematics', 'Mathematics for Class 10', 1, true, NOW(), NOW(), 'admin', 'admin'),
(2, 'Physics', 'Physics for Class 10', 1, true, NOW(), NOW(), 'admin', 'admin'),
(3, 'Chemistry', 'Chemistry for Class 11', 2, true, NOW(), NOW(), 'admin', 'admin'),
(4, 'Biology', 'Biology for Class 12', 3, true, NOW(), NOW(), 'admin', 'admin');

-- Insert sample Chapters (based on Chapter entity structure - NO chapter_order field)
INSERT INTO chapters (id, name, description, subject_id, is_active, created_at, updated_at, created_by, updated_by) VALUES
(1, 'Algebra', 'Basic Algebra concepts', 1, true, NOW(), NOW(), 'admin', 'admin'),
(2, 'Geometry', 'Geometric shapes and properties', 1, true, NOW(), NOW(), 'admin', 'admin'),
(3, 'Motion', 'Laws of motion', 2, true, NOW(), NOW(), 'admin', 'admin'),
(4, 'Organic Chemistry', 'Carbon compounds', 3, true, NOW(), NOW(), 'admin', 'admin');

-- Insert sample Questions (based on Question entity structure)
INSERT INTO questions (id, question_text, explanation, is_added_to_paper, is_active, section_type, question_type, difficulty_level, marks, negative_marks, chapter_id, created_at, updated_at, created_by, updated_by) VALUES
(1, 'What is the value of x in the equation 2x + 5 = 15?', 'Solve by isolating x: 2x = 15 - 5 = 10, so x = 5', false, true, 'MCQ', 'SINGLE_CHOICE', 'EASY', 1.0, 0.0, 1, NOW(), NOW(), 'admin', 'admin'),
(2, 'Calculate the area of a circle with radius 7 cm.', 'Use formula A = πr². A = π × 7² = 49π cm²', false, true, 'MCQ', 'SINGLE_CHOICE', 'MEDIUM', 2.0, 0.5, 2, NOW(), NOW(), 'admin', 'admin'),
(3, 'State Newton''s First Law of Motion.', 'An object at rest stays at rest and an object in motion stays in motion unless acted upon by an external force.', false, true, 'SHORT_QUESTION', 'DESCRIPTIVE', 'EASY', 3.0, 0.0, 3, NOW(), NOW(), 'admin', 'admin'),
(4, 'What is the molecular formula of methane?', 'CH₄ - One carbon atom bonded to four hydrogen atoms', false, true, 'MCQ', 'SINGLE_CHOICE', 'EASY', 1.0, 0.25, 4, NOW(), NOW(), 'admin', 'admin');

-- Insert MCQ Options for MCQ questions
INSERT INTO mcq_options (id, option_text, is_correct, option_order, question_id, created_at, updated_at, created_by, updated_by) VALUES
-- Options for Question 1 (Algebra)
(1, '5', true, 1, 1, NOW(), NOW(), 'admin', 'admin'),
(2, '3', false, 2, 1, NOW(), NOW(), 'admin', 'admin'),
(3, '7', false, 3, 1, NOW(), NOW(), 'admin', 'admin'),
(4, '10', false, 4, 1, NOW(), NOW(), 'admin', 'admin'),

-- Options for Question 2 (Geometry)
(5, '49π cm²', true, 1, 2, NOW(), NOW(), 'admin', 'admin'),
(6, '14π cm²', false, 2, 2, NOW(), NOW(), 'admin', 'admin'),
(7, '49 cm²', false, 3, 2, NOW(), NOW(), 'admin', 'admin'),
(8, '98π cm²', false, 4, 2, NOW(), NOW(), 'admin', 'admin'),

-- Options for Question 4 (Chemistry)
(9, 'CH₄', true, 1, 4, NOW(), NOW(), 'admin', 'admin'),
(10, 'C₂H₆', false, 2, 4, NOW(), NOW(), 'admin', 'admin'),
(11, 'CH₃OH', false, 3, 4, NOW(), NOW(), 'admin', 'admin'),
(12, 'C₆H₆', false, 4, 4, NOW(), NOW(), 'admin', 'admin');