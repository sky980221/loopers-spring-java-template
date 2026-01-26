# Commerce Ranking Platform

Redis + Kafka 기반의 **커머스 상품 랭킹 집계 플랫폼**입니다.  
대규모 이벤트 트래픽 환경에서도 **정확하고 안정적인 Top-N 랭킹 제공**을 목표로 합니다.

이 프로젝트는 단순 랭킹 기능 구현을 넘어,

- 이벤트 발행 신뢰성 (Transactional Outbox)
- Consumer 멱등 처리
- Batch 기반 MV(Materialized View) 갱신

까지 포함한 **실전형 이벤트 기반 아키텍처**를 다룹니다.

---

##  Key Features

- **Redis Sorted Set(ZSET)** 기반 실시간 Top-N 상품 랭킹 제공  
- **Kafka 이벤트 스트리밍** 기반 랭킹 집계 파이프라인 구축  
- **Transactional Outbox Pattern** 적용으로 이벤트 발행 안정성 확보  
- **Consumer 멱등 처리**로 중복 이벤트 및 재처리 상황 대응  
- **Spring Batch 기반 MV 갱신**으로 조회 성능 최적화  

---

##  Architecture Overview

```mermaid
sequenceDiagram
  autonumber
  participant API as Commerce API
  participant DB as RDBMS (Orders + Outbox)
  participant Outbox as Outbox Table
  participant Kafka as Kafka Broker
  participant Consumer as Ranking Consumer
  participant Redis as Redis Ranking (ZSET)

  API->>DB: 주문/상품 이벤트 저장
  API->>Outbox: Outbox 이벤트 기록
  Outbox->>Kafka: 이벤트 발행
  Kafka->>Consumer: 이벤트 소비
  Consumer->>Redis: 랭킹 집계 업데이트
