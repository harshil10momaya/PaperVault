CREATE TABLE programs (
    program_id SERIAL PRIMARY KEY,
    program_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    program_id INTEGER NOT NULL REFERENCES programs(program_id),
    course_code VARCHAR(10) NOT NULL UNIQUE,
    course_title VARCHAR(255) NOT NULL,
    semester INTEGER NOT NULL,
    UNIQUE (program_id, course_code)
);

CREATE TABLE students (
    student_id VARCHAR(15) PRIMARY KEY,
    program_id INTEGER NOT NULL REFERENCES programs(program_id),
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL 
);

CREATE TABLE papers (
    paper_id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(course_id),
    academic_year INTEGER NOT NULL,
    exam_type VARCHAR(10) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP DEFAULT NOW(),
    CONSTRAINT check_exam_type CHECK (exam_type IN ('CA1', 'CA2', 'SEM')),
    UNIQUE (course_id, academic_year, exam_type)
);

INSERT INTO programs (program_name) VALUES 
('23XT - Theoretical Computer Science'),
('23XD - Data Science'),
('23XC - Cyber Security'),
('23XW - Software Systems');

DELETE FROM courses;
DELETE FROM papers;

DO $$
DECLARE
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN
    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (1, '23XT51', 'Theory of Computing', 5),
    (1, '23XT52', 'Computational Number Theory and Cryptography', 5),
    (1, '23XT53', 'Machine Learning', 5),
    (1, '23XT54', 'Design and Analysis of Algorithms', 5),
    (1, '23XTE8', 'Big Data and Modern Database Systems', 5);

    INSERT INTO papers (course_id, academic_year, exam_type, file_path) VALUES
    ((SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA1', FILE_PATH_ROOT || '23XT51_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA2', FILE_PATH_ROOT || '23XT51_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA1', FILE_PATH_ROOT || '23XT52_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA2', FILE_PATH_ROOT || '23XT52_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA1', FILE_PATH_ROOT || '23XT53_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA2', FILE_PATH_ROOT || '23XT53_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA1', FILE_PATH_ROOT || '23XT54_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA2', FILE_PATH_ROOT || '23XT54_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA1', FILE_PATH_ROOT || '23XTE8_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA2', FILE_PATH_ROOT || '23XTE8_CA2.pdf');
END $$;

ALTER TABLE students DROP CONSTRAINT IF EXISTS students_program_id_fkey;

TRUNCATE TABLE programs RESTART IDENTITY CASCADE;

INSERT INTO programs (program_name) VALUES 
('23XT - Theoretical Computer Science'),
('23XD - Data Science'),
('23XC - Cyber Security'),
('23XW - Software Systems');

ALTER TABLE students 
ADD CONSTRAINT students_program_id_fkey 
FOREIGN KEY (program_id) REFERENCES programs (program_id);

DELETE FROM courses;
DELETE FROM papers;

INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
(1, '23XT51', 'Theory of Computing', 5),
(1, '23XT52', 'Computational Number Theory and Cryptography', 5),
(1, '23XT53', 'Machine Learning', 5),
(1, '23XT54', 'Design and Analysis of Algorithms', 5),
(1, '23XTE8', 'Big Data and Modern Database Systems', 5);

DO $$
DECLARE
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN
    DELETE FROM papers;
    DELETE FROM courses;

    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (1, '23XT51', 'Theory of Computing', 5),
    (1, '23XT52', 'Computational Number Theory and Cryptography', 5),
    (1, '23XT53', 'Machine Learning', 5),
    (1, '23XT54', 'Design and Analysis of Algorithms', 5),
    (1, '23XTE8', 'Big Data and Modern Database Systems', 5);

    INSERT INTO papers (course_id, academic_year, exam_type, file_path) VALUES
    ((SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA1', FILE_PATH_ROOT || '23XT51_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA2', FILE_PATH_ROOT || '23XT51_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA1', FILE_PATH_ROOT || '23XT52_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA2', FILE_PATH_ROOT || '23XT52_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA1', FILE_PATH_ROOT || '23XT53_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA2', FILE_PATH_ROOT || '23XT53_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA1', FILE_PATH_ROOT || '23XT54_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA2', FILE_PATH_ROOT || '23XT54_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA1', FILE_PATH_ROOT || '23XTE8_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA2', FILE_PATH_ROOT || '23XTE8_CA2.pdf');
END $$;

CREATE TABLE favorites (
    favorite_id SERIAL PRIMARY KEY,
    student_id VARCHAR(15) NOT NULL REFERENCES students(student_id),
    paper_id INTEGER NOT NULL REFERENCES papers(paper_id),
    UNIQUE (student_id, paper_id)
);

DO $$
DECLARE
    CYBER_SECURITY_PROGRAM_ID INTEGER := 3;
    SEMESTER_NUMBER INTEGER := 5;
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN
    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (CYBER_SECURITY_PROGRAM_ID, '23XC51', 'Network Security', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC52', 'Cryptanalysis', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC53', 'Machine Learning', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC54', 'Software Security and Exploitation', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XCE7', 'Artificial Intelligence', SEMESTER_NUMBER)
    ON CONFLICT (program_id, course_code) DO NOTHING;

    DELETE FROM papers 
    WHERE course_id IN (SELECT course_id FROM courses WHERE program_id = CYBER_SECURITY_PROGRAM_ID AND semester = SEMESTER_NUMBER);

    INSERT INTO papers (course_id, academic_year, exam_type, file_path) VALUES
    ((SELECT course_id FROM courses WHERE course_code = '23XC51'), 2025, 'CA1', FILE_PATH_ROOT || '23XC51_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC51'), 2025, 'CA2', FILE_PATH_ROOT || '23XC51_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC52'), 2025, 'CA1', FILE_PATH_ROOT || '23XC52_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC52'), 2025, 'CA2', FILE_PATH_ROOT || '23XC52_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC53'), 2025, 'CA1', FILE_PATH_ROOT || '23XC53_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC54'), 2025, 'CA1', FILE_PATH_ROOT || '23XC54_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XC54'), 2025, 'CA2', FILE_PATH_ROOT || '23XC54_CA2.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XCE7'), 2025, 'CA1', FILE_PATH_ROOT || '23XCE7_CA1.pdf'),
    ((SELECT course_id FROM courses WHERE course_code = '23XCE7'), 2025, 'CA2', FILE_PATH_ROOT || '23XCE7_CA2.pdf');
END $$;
