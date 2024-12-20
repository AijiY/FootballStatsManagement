# サッカースタッツ管理システム

## 作成背景（Background）
　SpringBootを使用したWebアプリケーション開発の学習成果として、自分の好きな国やリーグのサッカーの試合成績を管理できるRestAPIを用いたシステムを作成しました。

　RestAPIを使用するためのクライアントページは[こちら](https://github.com/AijiY/StatsManagement_react)で作成しています。

## 開発環境（Development Environment）
### 技術（Technologies）
![badge](https://img.shields.io/badge/Java-21.0.4-ED8B00?logo=openjdk&logoColor=white)
![badge](https://img.shields.io/badge/SpringBoot-3.3.3-6DB33F?logo=spring&logoColor=white)
![badge](https://img.shields.io/badge/MySQL-8.0.39-4479A1?logo=mysql&logoColor=white)
![badge](https://img.shields.io/badge/MyBatis-%23DC382D?logoColor=white)
![badge](https://img.shields.io/badge/Junit5-%2325A162?logo=junit5&logoColor=white)
![badge](https://img.shields.io/badge/H2-2.3.232-007396?logo=h2&logoColor=white)

### ツール（Tools）
![badge](https://img.shields.io/badge/IntelliJ_IDEA-2024.1.4-%23000000?logo=intellijidea&logoColor=white)
![badge](https://img.shields.io/badge/GitHub-%23181717?logo=github&logoColor=white)
![badge](https://img.shields.io/badge/GitHub_Actions-%232088FF?logo=githubactions&logoColor=white)
![badge](https://img.shields.io/badge/Swagger-%2385EA2D?logo=swagger&logoColor=white)
![badge](https://img.shields.io/badge/dbdiagram.io-007ACC?logo=appveyor&logoColor=white)
![badge](https://img.shields.io/badge/draw.io-FB9D3A?logo=diagramsdotnet&logoColor=white)
![badge](https://img.shields.io/badge/PlantUML-8A2BE2?logo=plantuml&logoColor=white)
![badge](https://img.shields.io/badge/Amazon%20Web%20Services-232F3E?logo=amazonwebservices&logoColor=white)


## 機能（Function）
- **登録** ：国～選手までの基本データ、および試合結果を登録できます。
- **検索** ：登録したデータおよびそのデータを用いて算定したデータの検索ができます。
- **更新** ：登録したデータを更新できます。

## ER図（Entity Relationship Diagram）
![ER](/docs/svg/ER.svg)

## ドメインクラス図（Domain Class Diagram）
![class](/docs/svg/class.svg)

## シーケンス図（Sequence Diagram）
### 基本フロー
![SD-basic_01](/docs/svg/SD-basic_01.svg)

### ドメインクラスの作成が必要な場合
![SD-basic_02](/docs/svg/SD-basic_02.svg)

## API仕様（API Specification）
[こちらのページ](http://54.248.239.107:8080/swagger-ui/index.html#/)にてAPI仕様を確認できます。

## テスト
下記テストをGithub Actionsで自動実行しています。
- 単体テスト
  - [Controller](https://github.com/AijiY/FootballStatsManagement/blob/main/src/test/java/football/StatsManagement/controller/FootballControllerTest.java)
  - Service ([FactoryService](https://github.com/AijiY/FootballStatsManagement/blob/main/src/test/java/football/StatsManagement/service/FactoryServiceTest.java) / [FootballService](https://github.com/AijiY/FootballStatsManagement/blob/main/src/test/java/football/StatsManagement/service/FootballServiceTest.java))
  - [Repository](https://github.com/AijiY/FootballStatsManagement/blob/main/src/test/java/football/StatsManagement/repository/FootballRepositoryTest.java)
- [結合テスト](https://github.com/AijiY/FootballStatsManagement/blob/main/src/test/java/football/StatsManagement/FootballIntegrationTest.java)

## 工夫した点 (Points to Note)
- **エンティティクラスのフィールド設定**<br>
データベース内には不要だが、クライアントサイドで表示する際に必要なデータをエンティティクラスに設定しました。また、そのために適切にコンストラクタを設定し、リクエスト種別によってコンストラクタを使い分けています。
- **不正なリクエストに対する例外処理**<br>
不正なリクエストに対しては、適切な例外処理を行い、ユーザーに分かりやすいエラーメッセージを返すようにしています。特に試合結果登録においては同時に登録する選手試合成績の内容と齟齬が生じないように15個超のビジネスロジック検証を実施しています。
- **簡潔なテストコードの作成**<br>
想定した例外処理をが発生するテストケースにおいて、@ParameterizedTestアノテーションをできるだけ使用しています。また、Controllerクラスのテストにおいては、複数のバリデーションエラーを同時に検証するprivateメソッドを作成し、テストコードの可読性を向上させています。

## 今後の課題（Future Improvements）
### 機能追加
- **検索機能** ：キーワード等での検索機能の追加
- **削除機能** ：エンティティ間のリレーションシップを考慮した削除機能の追加

### 例外処理メッセージ改善
- **可読性改善** ：特にバリデーションの@AssertTrueアノテーションによる例外処理メッセージの改善
- **表現の統一** ：ユーザーがメッセージに対して対処しやすいように、メッセージの表現を統一

## 反省点（Reflection Points）
- **スケジュール見積もりの甘さ**<br>
想定作業時間に対して余裕期間を同程度設けていたので、目標の1ヶ月での完成（クライアントページおよびAWSへのデプロイ含む）は達成できました。しかし、各タスクの作業時間の見積もり精度を向上させる必要があり、特にテストコードの作成において想定以上の時間を要したため、経験として今後の開発に活用します。
- **DB設計の不備**<br>
DBのカラム（エンティティクラスのフィールド）設定において、必要最小限のデータのみを設定することにしたため、クライアントページでのデータ取得および表示で不便を感じることがありました。工夫点で述べた通りエンティティクラスのフィールド設定により対処しましたが、DB設計時点で予め考慮しておくべき事案でした。

## AWS構成（AWS Configuration）
![AWS_API](/docs/svg/AWS_API.svg)

## License
This project is built with [Spring Boot](https://spring.io/projects/spring-boot) and is licensed under the [MIT License](LICENSE).
