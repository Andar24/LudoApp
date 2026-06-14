# 📚 Panduan Implementasi 4 Pilar OOP - Ludo Elite

## 🎯 Ringkasan Eksekutif

Proyek Ludo Elite ini telah mengimplementasikan **100% dari 4 pilar Pemrograman Berorientasi Objek** dengan cara yang mudah dijelaskan saat presentasi. Dokumen ini adalah panduan lengkap untuk menjelaskan setiap pilar dengan contoh kode konkret.

---

## 🏛️ 1. ENCAPSULATION (Enkapsulasi)

### Definisi
Enkapsulasi adalah pembungkusan data (atribut) dan method yang mengoperasikan data tersebut dalam satu unit (class), serta menyembunyikan detail implementasi internal dari luar.

### Implementasi dalam Ludo Elite

#### ✅ Contoh 1: Class `GamePiece`
```java
public abstract class GamePiece {
    // PRIVATE fields - tidak bisa diakses langsung dari luar
    private final int pieceNumber;
    private final PlayerColor ownerColor;
    private PieceState state;
    private int trackPosition;
    
    // PUBLIC getters - akses terkontrol
    public int getPieceNumber() {
        return pieceNumber;
    }
    
    public PieceState getState() {
        return state;
    }
    
    // PUBLIC setter dengan validasi
    public void setState(PieceState state) {
        this.state = state;
    }
}
```

**Penjelasan untuk Presentasi:**
- Field `pieceNumber`, `ownerColor`, `state`, dan `trackPosition` adalah **private**
- Tidak ada yang bisa langsung mengubah `pieceNumber` atau `ownerColor` dari luar
- Akses hanya melalui **getter/setter** yang terkontrol
- Ini melindungi data dari perubahan yang tidak valid

#### ✅ Contoh 2: Class `Player`
```java
public class Player {
    private final PlayerColor color;
    private final String playerName;
    private final List<GamePiece> pieces;
    private boolean isCurrentTurn;
    
    // Return copy untuk mencegah modifikasi eksternal
    public List<GamePiece> getPieces() {
        return new ArrayList<>(pieces); // Return copy!
    }
}
```

**Penjelasan untuk Presentasi:**
- Method `getPieces()` mengembalikan **copy** dari list, bukan referensi asli
- Ini mencegah pihak luar mengubah internal list tanpa kontrol
- Contoh **defensive copying** untuk enkapsulasi yang kuat

#### ✅ Contoh 3: Class `GameEngine`
```java
public class GameEngine {
    private final List<Player> players;
    private final Dice dice;
    private GameState gameState;
    private int consecutiveSixes;
    
    // Logic encapsulated - hanya bisa roll dice lewat method ini
    public int rollDice() {
        if (gameState != GameState.IN_PROGRESS || hasRolledDice) {
            return -1;
        }
        lastDiceValue = dice.roll();
        // ... logic lainnya
        return lastDiceValue;
    }
}
```

**Penjelasan untuk Presentasi:**
- Semua game state (players, dice, gameState) di-**encapsulate**
- Tidak ada akses langsung ke `consecutiveSixes` dari luar
- Semua operasi game harus melalui method publik yang ter-validasi

---

## 🌳 2. INHERITANCE (Pewarisan)

### Definisi
Inheritance adalah mekanisme di mana sebuah class dapat mewarisi atribut dan method dari class lain, menciptakan hierarki "is-a" relationship.

### Implementasi dalam Ludo Elite

#### ✅ Contoh 1: Hierarki GamePiece

```
           GamePiece (Abstract)
                 ↑
    ┌────────────┼────────────┐
    │            │            │
RedPiece    BluePiece    GreenPiece    YellowPiece
```

**Base Class (Parent):**
```java
public abstract class GamePiece {
    protected GamePiece(int pieceNumber, PlayerColor ownerColor) {
        this.pieceNumber = pieceNumber;
        this.ownerColor = ownerColor;
    }
    
    // Shared behavior
    public boolean canMove() {
        return state != PieceState.FINISHED;
    }
    
    public void sendToBase() {
        state = PieceState.IN_BASE;
        trackPosition = -1;
    }
    
    // Abstract method - harus diimplementasi child
    public abstract void render(GraphicsContext gc, double x, double y, double size);
}
```

**Child Class:**
```java
public class RedPiece extends GamePiece {
    public RedPiece(int pieceNumber) {
        super(pieceNumber, PlayerColor.RED); // Call parent constructor
    }
    
    @Override
    public void render(GraphicsContext gc, double x, double y, double size) {
        // Red-specific rendering
        gc.setFill(Color.web("#ef4444"));
        gc.fillOval(x, y, size, size);
    }
}
```

**Penjelasan untuk Presentasi:**
- `RedPiece`, `BluePiece`, `GreenPiece`, `YellowPiece` **mewarisi** dari `GamePiece`
- Mereka inherit semua field dan method dari parent (`canMove()`, `sendToBase()`, dll)
- Mereka **override** method `render()` dengan implementasi spesifik warna masing-masing
- **Relationship:** "RedPiece IS-A GamePiece" ✅

#### ✅ Contoh 2: Controller Hierarchy

```
       BaseController
             ↑
    ┌────────┼────────────┐
    │        │            │
LoginController  DashboardController  LudoBoardController
```

**Base Class:**
```java
public abstract class BaseController {
    protected void showError(String header, String content) {
        AlertHelper.showError("Error", header, content);
    }
    
    protected <T> void runAsync(Callable<T> task, Consumer<T> onSuccess, 
                                Consumer<Exception> onError, 
                                ProgressIndicator spinner) {
        // Shared async logic
    }
}
```

**Child Class:**
```java
public class LudoBoardController extends BaseController {
    // Inherit semua method dari BaseController
    // Bisa langsung call showError(), runAsync(), dll
}
```

**Penjelasan untuk Presentasi:**
- Semua controller **inherit** dari `BaseController`
- Mereka dapat langsung menggunakan `showError()` dan `runAsync()` tanpa reimplementasi
- Code reuse dan konsistensi behavior

---

## 🎭 3. POLYMORPHISM (Polimorfisme)

### Definisi
Polymorphism adalah kemampuan objek untuk mengambil banyak bentuk. Dalam OOP, ini berarti object dari subclass dapat diperlakukan sebagai object dari superclass, namun tetap menjalankan behavior spesifik subclass-nya.

### Implementasi dalam Ludo Elite

#### ✅ Contoh 1: Polymorphic Rendering

```java
// In BoardRenderer class
private void drawPiece(GraphicsContext gc, GamePiece piece) {
    Position pos = getPiecePosition(piece);
    if (pos != null) {
        // POLYMORPHIC CALL!
        // piece bisa RedPiece, BluePiece, GreenPiece, atau YellowPiece
        // Tapi kita tidak perlu tahu spesifik typenya
        // Setiap piece akan call render() versi mereka sendiri
        piece.render(gc, 
                    pos.getX() - PIECE_SIZE / 2, 
                    pos.getY() - PIECE_SIZE / 2, 
                    PIECE_SIZE);
    }
}
```

**Penjelasan untuk Presentasi:**
- Method `drawPiece()` menerima parameter **`GamePiece`** (tipe parent)
- Saat runtime, `piece` bisa jadi `RedPiece`, `BluePiece`, dst (tipe child)
- Saat `piece.render()` dipanggil, Java **otomatis call** versi render() yang sesuai dengan tipe asli object
- **RedPiece** → render dengan warna merah
- **BluePiece** → render dengan warna biru
- Ini adalah **Runtime Polymorphism** (Dynamic Dispatch)

#### ✅ Contoh 2: Factory Pattern dengan Polymorphism

```java
// In Player class
private GamePiece createPiece(PlayerColor color, int pieceNumber) {
    switch (color) {
        case RED:    return new RedPiece(pieceNumber);
        case BLUE:   return new BluePiece(pieceNumber);
        case GREEN:  return new GreenPiece(pieceNumber);
        case YELLOW: return new YellowPiece(pieceNumber);
    }
}

// Usage
private final List<GamePiece> pieces; // Polymorphic container

for (int i = 0; i < 4; i++) {
    pieces.add(createPiece(color, i)); // Add different types
}
```

**Penjelasan untuk Presentasi:**
- `List<GamePiece>` dapat menampung `RedPiece`, `BluePiece`, etc.
- Semua disimpan sebagai tipe `GamePiece` (parent)
- Saat dioperasikan, mereka tetap menjalankan behavior spesifik mereka
- **"One interface, multiple implementations"**

#### ✅ Contoh 3: Polymorphic Game Logic

```java
// In GameEngine
public boolean movePiece(GamePiece piece, int steps) {
    // piece bisa RedPiece, BluePiece, GreenPiece, atau YellowPiece
    // Tapi kita treat semuanya sebagai GamePiece
    
    if (piece.getState() == PieceState.IN_BASE) {
        // Logic sama untuk semua piece types
    }
    
    piece.setTrackPosition(newPosition); // Polymorphic call
    return true;
}
```

**Penjelasan untuk Presentasi:**
- `movePiece()` tidak perlu tahu tipe spesifik piece
- Semua piece diperlakukan sama karena mereka punya interface yang sama (inherited dari `GamePiece`)
- **Loose coupling** dan **extensibility** - mudah tambah warna baru tanpa ubah logic

---

## 🎨 4. ABSTRACTION (Abstraksi)

### Definisi
Abstraction adalah proses menyembunyikan detail implementasi kompleks dan hanya menampilkan fungsionalitas esensial kepada user. Dalam Java, ini dicapai dengan abstract class atau interface.

### Implementasi dalam Ludo Elite

#### ✅ Contoh 1: Abstract Class `GamePiece`

```java
public abstract class GamePiece {
    // Concrete fields
    private final int pieceNumber;
    private final PlayerColor ownerColor;
    
    // Concrete methods (shared implementation)
    public boolean canMove() {
        return state != PieceState.FINISHED;
    }
    
    public void sendToBase() {
        state = PieceState.IN_BASE;
        trackPosition = -1;
    }
    
    // ABSTRACT METHOD - no implementation!
    // Memaksa setiap child class untuk implement
    public abstract void render(GraphicsContext gc, double x, double y, double size);
}
```

**Penjelasan untuk Presentasi:**
- `GamePiece` adalah **abstract class** - tidak bisa di-instantiate langsung
- Tidak bisa: `GamePiece piece = new GamePiece(0, RED);` ❌
- Method `render()` adalah **abstract** - tidak punya body/implementasi
- Setiap child class **HARUS** implement `render()`
- Ini adalah **contract** - "jika kamu extend GamePiece, kamu HARUS bisa render dirimu"

#### ✅ Contoh 2: Abstract Controller Pattern

```java
public abstract class BaseController {
    // Template methods - shared behavior
    protected void showError(String header, String content) { ... }
    protected void showInfo(String header, String content) { ... }
    
    // Bisa tambah abstract method jika perlu
    // public abstract void initialize();
}
```

**Penjelasan untuk Presentasi:**
- `BaseController` abstract menyediakan **template** untuk semua controller
- Child controllers inherit behavior tanpa tahu detail implementasi
- **Separation of concerns** - BaseController handle utility, child handle specific logic

#### ✅ Contoh 3: Abstraction melalui Interface (Enum sebagai Contract)

```java
public enum PlayerColor {
    RED("#ef4444", "Red"),
    BLUE("#3b82f6", "Blue"),
    GREEN("#22c55e", "Green"),
    YELLOW("#f59e0b", "Yellow");
    
    private final String hexColor;
    private final String displayName;
    
    // Abstract away color details
    public String getHexColor() {
        return hexColor;
    }
    
    public PlayerColor getNext() {
        return values()[(this.ordinal() + 1) % values().length];
    }
}
```

**Penjelasan untuk Presentasi:**
- User tidak perlu tahu hex code warna
- Cukup call `color.getHexColor()`
- Internal representation disembunyikan
- Bisa ubah implementasi tanpa ubah interface

#### ✅ Contoh 4: Layer Abstraction (Separation of Concerns)

```
┌─────────────────────────────────────┐
│   View (FXML/BoardRenderer)         │  ← User sees this
├─────────────────────────────────────┤
│   Controller (LudoBoardController)  │  ← Handles UI events
├─────────────────────────────────────┤
│   Engine (GameEngine)               │  ← Game logic abstraction
├─────────────────────────────────────┤
│   Model (GamePiece, Player, etc.)   │  ← Data abstraction
└─────────────────────────────────────┘
```

**Penjelasan untuk Presentasi:**
- Setiap layer adalah **abstraction** dari kompleksitas di bawahnya
- Controller tidak tahu bagaimana GameEngine implement game rules
- GameEngine tidak tahu bagaimana GamePiece render dirinya
- **High cohesion, low coupling**

---

## 📊 Ringkasan Implementasi per Class

| Class | Encapsulation | Inheritance | Polymorphism | Abstraction |
|-------|--------------|-------------|--------------|-------------|
| `GamePiece` | ✅ Private fields | ✅ Abstract parent | ✅ Abstract method | ✅ Abstract class |
| `RedPiece` | ✅ Inherited | ✅ Extends GamePiece | ✅ Override render() | - |
| `BluePiece` | ✅ Inherited | ✅ Extends GamePiece | ✅ Override render() | - |
| `GreenPiece` | ✅ Inherited | ✅ Extends GamePiece | ✅ Override render() | - |
| `YellowPiece` | ✅ Inherited | ✅ Extends GamePiece | ✅ Override render() | - |
| `Player` | ✅ Private fields, defensive copy | - | ✅ Factory method | - |
| `GameEngine` | ✅ All game state private | - | ✅ Works with GamePiece polymorphically | - |
| `BoardRenderer` | ✅ Private track positions | - | ✅ Renders pieces polymorphically | ✅ Hides rendering complexity |
| `BaseController` | ✅ Protected utilities | ✅ Parent of controllers | - | ✅ Abstract template |
| `LudoBoardController` | ✅ Private game state | ✅ Extends BaseController | - | - |

---

## 🎤 Script Presentasi (Template)

### Slide 1: Encapsulation
"Mari kita lihat class GamePiece. Semua field-nya private, seperti pieceNumber dan state. Kita tidak bisa langsung ubah dari luar. Harus lewat getter dan setter. Ini contoh **Encapsulation** - data dilindungi, akses dikontrol."

### Slide 2: Inheritance
"Perhatikan hierarki ini. GamePiece adalah parent abstract class. RedPiece, BluePiece, GreenPiece, dan YellowPiece meng-extend GamePiece. Mereka mewarisi semua method seperti canMove() dan sendToBase(). Ini adalah **Inheritance** - code reuse dan hierarki is-a relationship."

### Slide 3: Polymorphism
"Lihat method drawPiece() di BoardRenderer. Parameter-nya tipe GamePiece, tapi saat runtime bisa RedPiece, BluePiece, atau lainnya. Saat kita call piece.render(), Java otomatis call versi yang sesuai. Ini **Polymorphism** - one interface, multiple implementations."

### Slide 4: Abstraction
"GamePiece adalah abstract class dengan abstract method render(). Kita tidak bisa buat instance GamePiece langsung. Setiap child HARUS implement render(). Ini memaksa consistency sekaligus menyembunyikan detail implementasi. Ini **Abstraction** - fokus pada 'apa' bukan 'bagaimana'."

---

## 🔧 Cara Compile & Run

```bash
# Compile
mvn clean compile

# Run
mvn javafx:run

# Atau gunakan file batch yang sudah ada
compile.bat
run.bat
```

---

## 📝 Checklist untuk Presentasi

- [ ] Jelaskan GamePiece hierarchy sebagai contoh Inheritance
- [ ] Tunjukkan private fields di GamePiece sebagai Encapsulation
- [ ] Demo polymorphic rendering - satu method render berbagai piece types
- [ ] Jelaskan abstract method render() sebagai Abstraction contract
- [ ] Tunjukkan factory pattern di Player class (Polymorphism)
- [ ] Demo gameplay - roll dice, move pieces, capture mechanism
- [ ] Jelaskan WebSocket hooks untuk integrasi future

---

## 🎯 Fitur Game yang Sudah Diimplementasi

✅ Papan Ludo lengkap dengan 4 base warna  
✅ Track 52 posisi dengan safe zones  
✅ Home lanes untuk setiap warna  
✅ Sistem dadu dengan aturan lengkap (6 untuk keluar, 3x6 skip turn)  
✅ Turn-based system  
✅ Capture mechanism  
✅ Win condition detection  
✅ Animasi dice roll  
✅ UI highlighting untuk current player  
✅ Hook untuk WebSocket integration (siap pakai)  

---

**Dokumen ini adalah panduan lengkap untuk presentasi OOP. Selamat presentasi! 🚀**
