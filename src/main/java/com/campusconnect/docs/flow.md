
```mermaid
sequenceDiagram
    actor Asker as Student (Asker)
    actor Solver as Student (Solver)
    participant App as App (UI)
    participant DB as FileStorageService

    Note over Asker, DB: Phase 1: The Request
    Asker->>App: Click "Ask Question"
    App->>DB: saveQuestion(content)
    DB-->>App: Success (Append to CSV)
    App-->>Solver: Update Queue View (Solver sees the question)

    Note over Asker, DB: Phase 2: The Resolution
    Solver->>App: Click "Answer & Resolve"
    App->>Solver: Prompt for Answer Text
    Solver->>App: Submits Answer ("Try restarting IntelliJ")
    App->>DB: resolveQuestion(question, answer)
    
    rect rgb(240, 240, 240)
        Note right of DB: Internal Transaction
        DB->>DB: Remove from questions.csv
        DB->>DB: Add to notifications.csv
    end
    
    DB-->>App: Success (Question removed from queue)

    Note over Asker, DB: Phase 3: The Result
    Asker->>App: Click "Open My Inbox"
    App->>DB: getNotifications(AskerId)
    DB-->>App: Return List
    App-->>Asker: Display Answer