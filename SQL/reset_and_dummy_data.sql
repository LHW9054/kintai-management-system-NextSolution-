-- ================================================================
-- KINTAI 開発用全初期化・ダミーデータ
-- 管理者1名・上司2名・人事1名・一般社員6名
-- 開発テスト基準：2026年7月勤務データ
-- 全テーブルを削除して再作成します.
-- ================================================================

CREATE DATABASE IF NOT EXISTS kintai_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kintai_db;
SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS approval_history;
DROP TABLE IF EXISTS audit_log;
DROP TABLE IF EXISTS work_edit_request;
DROP TABLE IF EXISTS leave_request;
DROP TABLE IF EXISTS attendance;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS work_type;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE department (
  dept_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  dept_name VARCHAR(100) NOT NULL,
  location VARCHAR(255) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE work_type (
  work_type_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type_name VARCHAR(100) NOT NULL,
  start_time TIME NOT NULL DEFAULT '09:00:00',
  end_time TIME NOT NULL DEFAULT '18:00:00',
  break_time_hours DECIMAL(4,2) DEFAULT 1.00,
  required_hours DECIMAL(4,2) DEFAULT 8.00
) ENGINE=InnoDB;

CREATE TABLE users (
  user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  employee_code VARCHAR(50) NOT NULL UNIQUE,
  name VARCHAR(100) NOT NULL,
  password VARCHAR(100) NOT NULL,
  role VARCHAR(30) NOT NULL,
  dept_id BIGINT NULL,
  manager_id BIGINT NULL,
  work_type_id BIGINT NULL,
  annual_paid_leave DECIMAL(5,2) NOT NULL DEFAULT 20.00,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_user_dept FOREIGN KEY (dept_id) REFERENCES department(dept_id),
  CONSTRAINT fk_user_manager FOREIGN KEY (manager_id) REFERENCES users(user_id),
  CONSTRAINT fk_user_work_type FOREIGN KEY (work_type_id) REFERENCES work_type(work_type_id)
) ENGINE=InnoDB;

CREATE TABLE attendance (
  attendance_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  work_date DATE NOT NULL,
  clock_in DATETIME NULL,
  clock_out DATETIME NULL,
  break_start DATETIME NULL,
  break_end DATETIME NULL,
  total_work_hours DECIMAL(6,2) DEFAULT 0,
  total_break_hours DECIMAL(6,2) DEFAULT 0,
  overtime_hours DECIMAL(6,2) DEFAULT 0,
  night_hours DECIMAL(6,2) DEFAULT 0,
  is_late BOOLEAN DEFAULT FALSE,
  is_fulfilled BOOLEAN DEFAULT FALSE,
  UNIQUE KEY uk_attendance_user_date(user_id,work_date),
  CONSTRAINT fk_att_user FOREIGN KEY (user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE leave_request (
  leave_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  leave_type VARCHAR(30) NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  days_count DECIMAL(5,1) NOT NULL,
  reason VARCHAR(1000),
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_leave_user FOREIGN KEY(user_id) REFERENCES users(user_id)
) ENGINE=InnoDB;

CREATE TABLE work_edit_request (
  edit_request_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL,
  attendance_id BIGINT NOT NULL,
  requested_clock_in DATETIME NOT NULL,
  requested_clock_out DATETIME NOT NULL,
  reason VARCHAR(1000),
  status VARCHAR(30) NOT NULL DEFAULT 'PENDING',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_edit_user FOREIGN KEY(user_id) REFERENCES users(user_id),
  CONSTRAINT fk_edit_att FOREIGN KEY(attendance_id) REFERENCES attendance(attendance_id)
) ENGINE=InnoDB;

CREATE TABLE approval_history (
  approval_history_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  request_type VARCHAR(30) NOT NULL,
  request_id BIGINT NOT NULL,
  requester_id BIGINT NOT NULL,
  approver_id BIGINT NULL,
  status VARCHAR(30) NOT NULL,
  comment VARCHAR(1000),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_approval_request(request_type,request_id)
) ENGINE=InnoDB;

CREATE TABLE audit_log (
  audit_log_id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NULL,
  action VARCHAR(100) NOT NULL,
  ip_address VARCHAR(100),
  details VARCHAR(2000),
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  INDEX idx_audit_created(created_at)
) ENGINE=InnoDB;

INSERT INTO department(dept_name,location) VALUES
('開発部','東京'),('営業部','東京'),('人事部','東京');

INSERT INTO work_type(type_name,start_time,end_time,break_time_hours,required_hours) VALUES
('通常勤務','09:00:00','18:00:00',1.00,8.00),
('時差勤務','10:00:00','19:00:00',1.00,8.00);

-- BCrypt: Admin123! / Manager123! / Employee123! / HR123456!
INSERT INTO users(employee_code,name,password,role,dept_id,manager_id,work_type_id,annual_paid_leave) VALUES
('admin','システム管理者','$2a$10$bXTw8kGmtB/kHXiFdSbwe.prELYzizDbYyoW4V9ZmvxwsXQ1RXFui','ADMIN',3,NULL,1,20.0),
('m001','佐藤 健一','$2a$10$QYI2mwRvHR8Y3gevgtPRUOGbVxy7ZGYkkdlUKOACrEfZxalhSzGB2','MANAGER',1,NULL,1,20.0),
('m002','鈴木 美咲','$2a$10$QYI2mwRvHR8Y3gevgtPRUOGbVxy7ZGYkkdlUKOACrEfZxalhSzGB2','MANAGER',2,NULL,1,20.0),
('hr001','高橋 直子','$2a$10$FQkFDRz2NIj742LrerBJGuubDDCIg1TFLxYs7k3cS48L05MwnUxv2','HR',3,NULL,1,20.0),
('e001','田中 太郎','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',1,2,1,20.0),
('e002','伊藤 翔','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',1,2,1,20.0),
('e003','山本 彩','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',1,2,1,20.0),
('e004','中村 拓海','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',2,3,1,20.0),
('e005','小林 優奈','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',2,3,1,20.0),
('e006','加藤 大輝','$2a$10$3igrI23yfpyMXktVCaOFDeNjLGMxa763ZsEo6azaYlapydQlfS2k.','EMPLOYEE',2,3,1,20.0);

-- 2026年7月平日中心のテスト用勤務記録
INSERT INTO attendance(user_id,work_date,clock_in,clock_out,break_start,break_end,total_work_hours,total_break_hours,overtime_hours,night_hours,is_late,is_fulfilled)
SELECT u.user_id, d.work_date,
       TIMESTAMP(d.work_date, CASE WHEN MOD(DAY(d.work_date),7)=0 THEN '09:07:00' ELSE '09:00:00' END),
       TIMESTAMP(d.work_date, CASE WHEN MOD(DAY(d.work_date),5)=0 THEN '18:30:00' ELSE '18:00:00' END),
       TIMESTAMP(d.work_date,'12:00:00'),TIMESTAMP(d.work_date,'13:00:00'),
       CASE WHEN MOD(DAY(d.work_date),5)=0 THEN 8.50 ELSE 8.00 END,1.00,
       CASE WHEN MOD(DAY(d.work_date),5)=0 THEN 0.50 ELSE 0.00 END,0.00,
       CASE WHEN MOD(DAY(d.work_date),7)=0 THEN TRUE ELSE FALSE END,TRUE
FROM users u
JOIN (SELECT DATE('2026-07-01') + INTERVAL seq DAY AS work_date FROM (
  SELECT 0 seq UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10 UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) x) d
WHERE u.role='EMPLOYEE' AND DAYOFWEEK(d.work_date) BETWEEN 2 AND 6;

INSERT INTO leave_request(user_id,leave_type,start_date,end_date,days_count,reason,status) VALUES
(5,'PAID','2026-07-15','2026-07-16',2.0,'私用のため','APPROVED'),
(6,'PAID','2026-07-20','2026-07-20',1.0,'通院のため','APPROVED'),
(7,'PAID','2026-07-27','2026-07-28',2.0,'家族行事のため','PENDING'),
(8,'PAID','2026-07-24','2026-07-24',1.0,'私用のため','PENDING');

INSERT INTO audit_log(user_id,action,ip_address,details) VALUES
(1,'LOGIN','127.0.0.1','開発用初期ログイン'),
(2,'LOGIN','127.0.0.1','上司テストログイン'),
(5,'LEAVE_APPLY','127.0.0.1','休暇申請テスト');

-- テストアカウント
-- admin / Admin123!
-- m001 / Manager123!
-- m002 / Manager123!
-- hr001 / HR123456!
-- e001~e006 / Employee123!
