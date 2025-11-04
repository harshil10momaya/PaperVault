-- DDL SCRIPT: Create Tables (Assuming database 'papervault_db' is connected)

-- 1. Create PROGRAMS table
CREATE TABLE programs (
    program_id SERIAL PRIMARY KEY,
    program_name VARCHAR(100) NOT NULL UNIQUE
);

-- 2. Create COURSES table
CREATE TABLE courses (
    course_id SERIAL PRIMARY KEY,
    program_id INTEGER NOT NULL REFERENCES programs(program_id),
    course_code VARCHAR(10) NOT NULL UNIQUE,
    course_title VARCHAR(255) NOT NULL,
    semester INTEGER NOT NULL,
    UNIQUE (program_id, course_code)
);

-- 3. Create STUDENTS table (for basic login)
CREATE TABLE students (
    student_id VARCHAR(15) PRIMARY KEY,
    program_id INTEGER NOT NULL REFERENCES programs(program_id),
    name VARCHAR(100) NOT NULL,
    password_hash VARCHAR(255) NOT NULL 
);

-- 4. Create PAPERS table (The core data)
CREATE TABLE papers (
    paper_id SERIAL PRIMARY KEY,
    course_id INTEGER NOT NULL REFERENCES courses(course_id),
    academic_year INTEGER NOT NULL,
    exam_type VARCHAR(10) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    upload_date TIMESTAMP DEFAULT NOW(),
    CONSTRAINT check_exam_type 
        CHECK (exam_type IN ('CA1', 'CA2', 'SEM')),
    UNIQUE (course_id, academic_year, exam_type)
);


-- DML SCRIPT: Insert Initial Data

-- 5. Insert Initial Programs (This sets program_id 1, 2, 3, 4)
INSERT INTO programs (program_name) VALUES 
('Theoretical Computer Science'),
('Data Science'),
('Cyber Security'),
('Software Systems');

-- 6. Update Program Names with Official Codes
UPDATE programs SET program_name = '23XT - Theoretical Computer Science' WHERE program_id = 1;
UPDATE programs SET program_name = '23XD - Data Science' WHERE program_id = 2;
UPDATE programs SET program_name = '23XC - Cyber Security' WHERE program_id = 3;
UPDATE programs SET program_name = '23XW - Software Systems' WHERE program_id = 4;

-- 7. CLEAR existing courses and insert new TCS Semester 5 courses
-- *** IMPORTANT: ADD THIS DELETE STATEMENT TO AVOID RERUN ISSUES ***
DELETE FROM courses; 

-- =================================================================================
-- 1. Setup Block (Required for using variables and handling multiple statements)
-- =================================================================================
DO $$
DECLARE
    -- Setting the specific path to your 'papers' folder as requested.
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN

    -- =============================================================================
    -- 2. Clean and Insert Courses (Ensures correct state before inserting papers)
    -- =============================================================================
    
    -- Delete existing papers first, as they rely on course_id
    DELETE FROM papers;
    -- Delete existing courses
    DELETE FROM courses; 
    
    -- Insert the five TCS (Program ID 1) Semester 5 subjects
    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (1, '23XT51', 'Theory of Computing', 5),
    (1, '23XT52', 'Computational Number Theory and Cryptography', 5),
    (1, '23XT53', 'Machine Learning', 5),
    (1, '23XT54', 'Design and Analysis of Algorithms', 5),
    (1, '23XTE8', 'Big Data and Modern Database Systems', 5);

    -- =============================================================================
    -- 3. Insert All 10 Paper Records
    -- =============================================================================
    
    -- Uses subqueries to automatically find the correct course_id for the file metadata
    INSERT INTO papers (course_id, academic_year, exam_type, file_path)
    VALUES
    
    -- 23XT51: Theory of Computing (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA1', FILE_PATH_ROOT || '23XT51_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA2', FILE_PATH_ROOT || '23XT51_CA2.pdf' ), -- Added CA2
    
    -- 23XT52: Computational Number Theory and Cryptography (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA1', FILE_PATH_ROOT || '23XT52_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA2', FILE_PATH_ROOT || '23XT52_CA2.pdf' ),
    
    -- 23XT53: Machine Learning (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA1', FILE_PATH_ROOT || '23XT53_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA2', FILE_PATH_ROOT || '23XT53_CA2.pdf' ),
    
    -- 23XT54: Design and Analysis of Algorithms (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA1', FILE_PATH_ROOT || '23XT54_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA2', FILE_PATH_ROOT || '23XT54_CA2.pdf' ),
    
    -- 23XTE8: Big Data and Modern Database Systems (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA1', FILE_PATH_ROOT || '23XTE8_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA2', FILE_PATH_ROOT || '23XTE8_CA2.pdf' ); -- Added CA2
    
    RAISE NOTICE 'Successfully inserted 5 courses and 10 paper records for testing.';

END $$;


-- DML Script to clean and re-insert program data correctly

-- Temporarily remove foreign key constraint to allow deletion of programs
ALTER TABLE students DROP CONSTRAINT students_program_id_fkey;

-- 1. Clear all existing program entries (both old and new coded ones)
TRUNCATE TABLE programs RESTART IDENTITY CASCADE;

-- 2. Insert ONLY the coded names, ensuring IDs 1, 2, 3, 4 are clean
INSERT INTO programs (program_name) VALUES 
('23XT - Theoretical Computer Science'),
('23XD - Data Science'),
('23XC - Cyber Security'),
('23XW - Software Systems');

-- Restore the foreign key constraint
ALTER TABLE students 
ADD CONSTRAINT students_program_id_fkey 
FOREIGN KEY (program_id) REFERENCES programs (program_id);

-- =================================================================================
-- DML SCRIPT: CLEANUP AND RESET PROGRAM DATA
-- =================================================================================

-- 1. Temporarily remove foreign key constraint from students table
--    This allows us to safely delete from the programs table.
ALTER TABLE students 
DROP CONSTRAINT IF EXISTS students_program_id_fkey;

-- 2. Clear all existing program entries (TRUNCATE resets the SERIAL ID counter)
TRUNCATE TABLE programs RESTART IDENTITY CASCADE;

-- 3. Insert ONLY the correct, coded program names
INSERT INTO programs (program_name) VALUES 
('23XT - Theoretical Computer Science'),
('23XD - Data Science'),
('23XC - Cyber Security'),
('23XW - Software Systems');

-- 4. Restore the foreign key constraint
ALTER TABLE students 
ADD CONSTRAINT students_program_id_fkey 
FOREIGN KEY (program_id) REFERENCES programs (program_id);

-- 5. IMPORTANT: You must re-insert your courses (Program ID 1 is 23XT)
--    This ensures courses link to the new, clean program_ids (1, 2, 3, 4).
DELETE FROM courses; 

INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
(1, '23XT51', 'Theory of Computing', 5),
(1, '23XT52', 'Computational Number Theory and Cryptography', 5),
(1, '23XT53', 'Machine Learning', 5),
(1, '23XT54', 'Design and Analysis of Algorithms', 5),
(1, '23XTE8', 'Big Data and Modern Database Systems', 5);

-- 6. Clean and re-insert the testing paper data (Optional, but recommended)
DELETE FROM papers;

-- NOTE: You must manually re-register your student users (e.g., ADMIN001) in the app after running this script.

COMMIT;

-- =================================================================================
-- DML SCRIPT: INSERT ALL 10 PAPER RECORDS
-- This script cleans existing papers/courses and inserts all provided data.
-- =================================================================================
DO $$
DECLARE
    -- Path must point to the folder containing your PDF files.
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN

    -- 1. Clean existing papers and courses
    -- This step is essential to ensure clean foreign key links after TRUNCATE in Step 29.
    DELETE FROM papers;
    DELETE FROM courses; 
    
    -- 2. Re-insert the five TCS (Program ID 1) Semester 5 subjects (Ensures IDs are sequential and correct)
    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (1, '23XT51', 'Theory of Computing', 5),
    (1, '23XT52', 'Computational Number Theory and Cryptography', 5),
    (1, '23XT53', 'Machine Learning', 5),
    (1, '23XT54', 'Design and Analysis of Algorithms', 5),
    (1, '23XTE8', 'Big Data and Modern Database Systems', 5);

    -- 3. Insert All 10 Paper Records
    INSERT INTO papers (course_id, academic_year, exam_type, file_path)
    VALUES
    -- 23XT51: Theory of Computing (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA1', FILE_PATH_ROOT || '23XT51_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT51'), 2025, 'CA2', FILE_PATH_ROOT || '23XT51_CA2.pdf' ),
    
    -- 23XT52: Computational Number Theory and Cryptography (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA1', FILE_PATH_ROOT || '23XT52_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT52'), 2025, 'CA2', FILE_PATH_ROOT || '23XT52_CA2.pdf' ),
    
    -- 23XT53: Machine Learning (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA1', FILE_PATH_ROOT || '23XT53_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT53'), 2025, 'CA2', FILE_PATH_ROOT || '23XT53_CA2.pdf' ),
    
    -- 23XT54: Design and Analysis of Algorithms (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA1', FILE_PATH_ROOT || '23XT54_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XT54'), 2025, 'CA2', FILE_PATH_ROOT || '23XT54_CA2.pdf' ),
    
    -- 23XTE8: Big Data and Modern Database Systems (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA1', FILE_PATH_ROOT || '23XTE8_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XTE8'), 2025, 'CA2', FILE_PATH_ROOT || '23XTE8_CA2.pdf' );
    
    RAISE NOTICE 'Successfully inserted 5 courses and 10 paper records for testing.';

END $$;

-- DDL Script to create the FAVORITES table
CREATE TABLE favorites (
    favorite_id SERIAL PRIMARY KEY,
    student_id VARCHAR(15) NOT NULL REFERENCES students(student_id),
    paper_id INTEGER NOT NULL REFERENCES papers(paper_id),
    
    -- Constraint: A student can only favorite a paper once
    UNIQUE (student_id, paper_id)
);

SELECT * FROM courses WHERE program_id = 1 AND semester = 5;

-- =================================================================================
-- DML SCRIPT: INSERT CYBER SECURITY (23XC) SEMESTER 5 DATA
-- =================================================================================
DO $$
DECLARE
    -- Define Constants
    CYBER_SECURITY_PROGRAM_ID INTEGER := 3; -- Assumed ID for '23XC - Cyber Security'
    SEMESTER_NUMBER INTEGER := 5;
    FILE_PATH_ROOT VARCHAR := 'C:/Users/LENOVO/Desktop/PaperVault/papers/'; 
BEGIN
    
    -- 1. Insert Courses for Cyber Security (Program ID 3, Semester 5)
    -- WARNING: These course codes must be unique across all programs (which they are).

    RAISE NOTICE 'Inserting Cyber Security (23XC) Semester 5 Courses...';

    INSERT INTO courses (program_id, course_code, course_title, semester) VALUES 
    (CYBER_SECURITY_PROGRAM_ID, '23XC51', 'Network Security', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC52', 'Cryptanalysis', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC53', 'Machine Learning', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XC54', 'Software Security and Exploitation', SEMESTER_NUMBER),
    (CYBER_SECURITY_PROGRAM_ID, '23XCE7', 'Artificial Intelligence', SEMESTER_NUMBER)
    ON CONFLICT (program_id, course_code) DO NOTHING; -- Prevents errors if courses already exist

    -- 2. Delete existing CS papers (if any) to prevent duplication errors
    DELETE FROM papers 
    WHERE course_id IN (SELECT course_id FROM courses WHERE program_id = CYBER_SECURITY_PROGRAM_ID AND semester = SEMESTER_NUMBER);

    RAISE NOTICE 'Inserting 9 Cyber Security paper records...';

    INSERT INTO papers (course_id, academic_year, exam_type, file_path)
    VALUES
    
    -- 23XC51: Network Security (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XC51'), 2025, 'CA1', FILE_PATH_ROOT || '23XC51_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XC51'), 2025, 'CA2', FILE_PATH_ROOT || '23XC51_CA2.pdf' ),
    
    -- 23XC52: Cryptanalysis (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XC52'), 2025, 'CA1', FILE_PATH_ROOT || '23XC52_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XC52'), 2025, 'CA2', FILE_PATH_ROOT || '23XC52_CA2.pdf' ),
    
    -- 23XC53: Machine Learning (1 paper)
    ( (SELECT course_id FROM courses WHERE course_code = '23XC53'), 2025, 'CA1', FILE_PATH_ROOT || '23XC53_CA1.pdf' ),
    
    -- 23XC54: Software Security and Exploitation (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XC54'), 2025, 'CA1', FILE_PATH_ROOT || '23XC54_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XC54'), 2025, 'CA2', FILE_PATH_ROOT || '23XC54_CA2.pdf' ),
    
    -- 23XCE7: Artificial Intelligence (2 papers)
    ( (SELECT course_id FROM courses WHERE course_code = '23XCE7'), 2025, 'CA1', FILE_PATH_ROOT || '23XCE7_CA1.pdf' ),
    ( (SELECT course_id FROM courses WHERE course_code = '23XCE7'), 2025, 'CA2', FILE_PATH_ROOT || '23XCE7_CA2.pdf' );
    
    RAISE NOTICE 'CS (23XC) Semester 5 data insertion complete.';

END $$;
