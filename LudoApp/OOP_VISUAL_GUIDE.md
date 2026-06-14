# 🎨 Visual Guide - 4 Pilar OOP di Ludo Elite

## Diagram untuk Presentasi

---

## 1️⃣ ENCAPSULATION

```
┌─────────────────────────────────────────────┐
│          GamePiece Class                    │
├─────────────────────────────────────────────┤
│  PRIVATE FIELDS (❌ No Direct Access)       │
│  - pieceNumber                              │
│  - ownerColor                               │
│  - state                                    │
│  - trackPosition                            │
├─────────────────────────────────────────────┤
│  PUBLIC METHODS (✅ Controlled Access)      │
│  + getPieceNumber()                         │
│  + getOwnerColor()                          │
│  + getState()                               │
│  + setState(PieceState)                     │
│  + getTrackPosition()                       │
│  + setTrackPosition(int)                    │
└─────────────────────────────────────────────┘

         Outside World 🌍
              │
              │ Can only access through
              │ public methods
              ↓
         ✅ Controlled
         ✅ Validated
         ✅ Safe
```

**Key Point:** Data disembunyikan, akses dikontrol

---

## 2️⃣ INHERITANCE

```
                    ┌─────────────────┐
                    │   GamePiece     │
                    │   (Abstract)    │
                    ├─────────────────┤
                    │ + canMove()     │
                    │ + sendToBase()  │
                    │ + render() ⚡   │ ← Abstract method
                    └────────┬────────┘
                             │
                             │ extends
         ┌───────────────────┼───────────────────┐
         │                   │                   │
    ┌────▼─────┐       ┌────▼─────┐       ┌────▼─────┐
    │RedPiece  │       │BluePiece │       │GreenPiece│
    ├──────────┤       ├──────────┤       ├──────────┤
    │Inherits: │       │Inherits: │       │Inherits: │
    │• canMove │       │• canMove │       │• canMove │
    │• sendTo  │       │• sendTo  │       │• sendTo  │
    │          │       │          │       │          │
    │Override: │       │Override: │       │Override: │
    │• render()│       │• render()│       │• render()│
    │  (RED)   │       │  (BLUE)  │       │  (GREEN) │
    └──────────┘       └──────────┘       └──────────┘

         │                   │                   │
         └───────────────────┴───────────────────┘
                             │
                    Shared Behavior ✅
                    + Individual Implementation ✅
```

**Key Point:** Code reuse + Specialization

---

## 3️⃣ POLYMORPHISM

```
Step 1: Store different types in same container
┌──────────────────────────────────────┐
│  List<GamePiece> pieces              │
│  ┌─────────────────────────────────┐ │
│  │ [0] RedPiece                    │ │
│  │ [1] BluePiece                   │ │
│  │ [2] GreenPiece                  │ │
│  │ [3] YellowPiece                 │ │
│  └─────────────────────────────────┘ │
└──────────────────────────────────────┘

Step 2: Call method without knowing exact type
┌──────────────────────────────────────┐
│  for (GamePiece piece : pieces) {    │
│      piece.render(gc, x, y, size);   │ ← Same call
│  }                                   │
└──────────────────────────────────────┘
         │
         ├──→ RedPiece.render()    → Red circle
         ├──→ BluePiece.render()   → Blue circle
         ├──→ GreenPiece.render()  → Green circle
         └──→ YellowPiece.render() → Yellow circle
              
              Different Behavior! ✨

Runtime Magic: Java automatically calls
the correct version based on actual type!
```

**Key Point:** One interface, multiple implementations

---

## 4️⃣ ABSTRACTION

```
┌─────────────────────────────────────────┐
│     GamePiece (Abstract Class)          │
│                                         │
│  ❌ Cannot do: new GamePiece()          │
│                                         │
│  ✅ Must extend to use                  │
├─────────────────────────────────────────┤
│  Concrete Methods:                      │
│  ✅ canMove() { ... }     (Has code)    │
│  ✅ sendToBase() { ... }  (Has code)    │
│                                         │
│  Abstract Method:                       │
│  ⚡ render() (NO CODE!)                 │
│     Every child MUST implement!         │
└─────────────────────────────────────────┘
                  │
                  │ Contract: "If you want to
                  │ be a GamePiece, you MUST
                  │ implement render()"
                  │
        ┌─────────┴─────────┐
        │                   │
   ┌────▼─────┐       ┌────▼─────┐
   │RedPiece  │       │BluePiece │
   │          │       │          │
   │✅ render│       │✅ render│
   │ {code}  │       │ {code}  │
   └──────────┘       └──────────┘
   
   Enforced Consistency! ✅
```

**Key Point:** Hide complexity, enforce contracts

---

## 🔄 All 4 Pillars Working Together

```
┌─────────────────────────────────────────────────────────┐
│                    BoardRenderer                        │
│  (Uses all pieces without knowing their types)          │
└────────────────────────┬────────────────────────────────┘
                         │
                         │ Polymorphism: Works with GamePiece
                         │
                         ↓
             ┌───────────────────────┐
             │    GamePiece          │ ← Abstraction: Abstract contract
             │    (Abstract)         │ ← Encapsulation: Private fields
             └───────────┬───────────┘
                         │
                         │ Inheritance: Parent-child relationship
         ┌───────────────┼───────────────┐
         │               │               │
    ┌────▼────┐    ┌────▼────┐    ┌────▼────┐
    │RedPiece │    │BluePiece│    │GreenPiece│
    │         │    │         │    │         │
    │Concrete │    │Concrete │    │Concrete │
    │Impl.    │    │Impl.    │    │Impl.    │
    └─────────┘    └─────────┘    └─────────┘

Result: Flexible, Maintainable, Extensible Code! 🎯
```

---

## 📊 Quick Comparison Table

| Pilar | Tujuan | Contoh di Ludo | Benefit |
|-------|--------|----------------|---------|
| **Encapsulation** | Hide data, control access | GamePiece private fields | Data protection |
| **Inheritance** | Code reuse, hierarchy | RedPiece extends GamePiece | Less duplicate code |
| **Polymorphism** | One interface, many forms | piece.render() different results | Flexibility |
| **Abstraction** | Hide complexity, enforce contract | GamePiece abstract render() | Consistency |

---

## 🎯 Real-World Analogy

### Encapsulation = Bank ATM
```
┌─────────────────────┐
│   ATM Machine       │
│                     │
│  [Withdraw] ←─── You can only use buttons
│  [Deposit]           
│  [Check Balance]    
│                     │
│  Internal Mechanism │ ←─── You CANNOT access this
│  (Hidden)           │      directly!
└─────────────────────┘
```

### Inheritance = Family Tree
```
    Grandparent (GamePiece)
         │
    ┌────┴────┐
  Parent1   Parent2
    │         │
  Child1    Child2 (RedPiece)
  
  Child inherits genes (methods) from parent!
```

### Polymorphism = Universal Remote
```
One Remote (method call)
    │
    ├──→ TV → Turn on TV
    ├──→ AC → Turn on AC
    └──→ Fan → Turn on Fan
    
    Same button, different devices!
```

### Abstraction = Car Interface
```
┌─────────────────────┐
│  Steering Wheel     │ ← Simple interface
│  Gas Pedal          │
│  Brake Pedal        │
└─────────────────────┘
         │
         │ Hides...
         ↓
┌─────────────────────┐
│  Engine             │ ← Complex implementation
│  Transmission       │
│  Fuel System        │
│  etc...             │
└─────────────────────┘
```

---

## 💡 Demo Script Points

### Point 1: Show Encapsulation
```java
// ❌ Cannot do this:
piece.pieceNumber = 10;  // Compile error!

// ✅ Must do this:
int num = piece.getPieceNumber();  // Controlled access
```

### Point 2: Show Inheritance
```java
// RedPiece has all GamePiece methods
RedPiece red = new RedPiece(0);
red.canMove();      // ← Inherited from GamePiece
red.sendToBase();   // ← Inherited from GamePiece
red.render(...);    // ← Overridden in RedPiece
```

### Point 3: Show Polymorphism
```java
// Store different types
GamePiece piece1 = new RedPiece(0);
GamePiece piece2 = new BluePiece(0);

// Same code, different behavior
piece1.render(...);  // → Red circle
piece2.render(...);  // → Blue circle
```

### Point 4: Show Abstraction
```java
// ❌ Cannot do this:
GamePiece piece = new GamePiece(0, RED);  // Error!

// ✅ Must create concrete type:
GamePiece piece = new RedPiece(0);  // OK!

// And MUST implement render()
```

---

## 🎓 Questions & Answers

### Q: "Mengapa pakai abstract class?"
**A:** "Untuk memaksa setiap warna piece implement render() dengan cara mereka sendiri, sambil share behavior umum seperti canMove()."

### Q: "Apa bedanya dengan interface?"
**A:** "Abstract class bisa punya concrete methods (canMove, sendToBase). Interface hanya contract murni. Kita butuh shared behavior, jadi pakai abstract class."

### Q: "Mengapa tidak bikin semua field public?"
**A:** "Encapsulation melindungi data dari perubahan yang tidak valid. Contoh: pieceNumber tidak boleh berubah setelah dibuat, jadi kita bikin final dan tidak ada setter."

### Q: "Apa keuntungan polymorphism?"
**A:** "BoardRenderer tidak perlu tahu tipe spesifik piece. Bisa tambah warna baru (PurplePiece) tanpa ubah BoardRenderer code. Loose coupling!"

---

## 🖨️ Print-Friendly Cheat Sheet

```
╔════════════════════════════════════════════════╗
║     LUDO ELITE - OOP CHEAT SHEET              ║
╠════════════════════════════════════════════════╣
║ 1. ENCAPSULATION                               ║
║    Private fields + Public methods             ║
║    Example: GamePiece.pieceNumber (private)    ║
║                                                ║
║ 2. INHERITANCE                                 ║
║    GamePiece → RedPiece (extends)              ║
║    Child inherits parent behavior              ║
║                                                ║
║ 3. POLYMORPHISM                                ║
║    piece.render() → different implementations  ║
║    One call, many forms                        ║
║                                                ║
║ 4. ABSTRACTION                                 ║
║    abstract class GamePiece                    ║
║    abstract method render()                    ║
║    Hide complexity, enforce contract           ║
╚════════════════════════════════════════════════╝
```

---

**Gunakan diagram-diagram ini untuk:**
- Presentasi slide
- Poster demo
- Penjelasan kepada dosen
- Backup jika live demo gagal

**Status: READY TO PRESENT! 🎤**
