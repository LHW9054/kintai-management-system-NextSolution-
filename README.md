# 勤怠管理システム

社員の出勤・退勤、休憩時間、休暇申請を管理する学習用Webアプリケーションです。

一般社員、上司、人事部、システム管理者の権限を設定し、権限に応じた機能を利用できます。

## 主な機能

- ログイン・ログアウト
- 出勤・退勤の登録
- 休憩開始・休憩終了の登録
- 勤務時間、残業時間、深夜時間の計算
- 月別勤務状況の確認
- 休暇申請と承認
- 勤務時間修正申請
- 上司による直属部下の勤怠確認
- ユーザー・部署管理
- 操作ログの確認

## 使用技術

| 分類 | 技術 |
| --- | --- |
| バックエンド | Java 17、Spring Boot |
| フロントエンド | HTML、CSS、JavaScript |
| データベース | MySQL |
| DBアクセス | Spring Data JPA、Hibernate |
| ビルドツール | Maven |

## システム構成

```text
Web画面
  ↓ API
Controller
  ↓
Service
  ↓
Repository
  ↓
MySQL
```

## 主なテーブル

| テーブル | 役割 |
| --- | --- |
| `users` | 社員情報 |
| `department` | 部署情報 |
| `work_type` | 勤務形態 |
| `attendance` | 出勤・退勤・休憩記録 |
| `leave_request` | 休暇申請 |
| `work_edit_request` | 勤務時間修正申請 |
| `approval_history` | 承認履歴 |
| `audit_log` | 操作履歴 |

## 実行方法

### 1. 必要な環境

- Java 17
- MySQL
- Maven（Maven Wrapperを同梱）

### 2. データベースの準備

MySQLで次のSQLファイルを実行します。

```text
sql/reset_and_dummy_data.sql
```

このSQLは`kintai_db`データベースとテストデータを作成します。
既存の同名テーブルを削除するため、学習・テスト環境でのみ使用してください。

### 3. データベース接続設定

環境に合わせて、`src/main/resources/application.properties`のMySQL接続情報を設定します。

公開リポジトリには実際のパスワードを登録せず、環境変数またはローカル専用設定ファイルを使用してください。

### 4. アプリケーションの起動

macOS・Linux：

```bash
./mvnw spring-boot:run
```

Windows：

```powershell
mvnw.cmd spring-boot:run
```

起動後、ブラウザで以下にアクセスします。

```text
http://localhost:8080/login.html
```

## テストアカウント

以下はダミーデータ専用のアカウントです。

| 権限 | ID | パスワード |
| --- | --- | --- |
| システム管理者 | `admin` | `Admin123!` |
| 上司 | `m001` | `Manager123!` |
| 人事部 | `hr001` | `HR123456!` |
| 一般社員 | `e001` | `Employee123!` |

## 今後の改善点

- Spring Securityを利用した認証・認可
- セッション管理とCSRF対策
- 入力チェックとエラー処理の強化
- スマートフォン表示への対応
- CSV・Excel出力
- 自動テストの追加

## 注意事項

本プロジェクトはJavaとSpring Bootの学習を目的として作成したものです。
実際の個人情報や本番環境の認証情報は使用しないでください。
