# 🔧 Cara Compile dan Run Ludo Elite

## Prerequisites

Pastikan sudah terinstall:
- ✅ Java 11 atau lebih tinggi
- ✅ Maven (untuk dependency management)
- ✅ JavaFX SDK (biasanya sudah include di Maven dependencies)

## Opsi 1: Menggunakan Batch Files (Windows)

### Compile
```bash
compile.bat
```

### Run
```bash
run.bat
```

## Opsi 2: Menggunakan Maven Command

### Compile
```bash
mvn clean compile
```

### Run
```bash
mvn javafx:run
```

## Opsi 3: Menggunakan IDE (IntelliJ IDEA / Eclipse)

### IntelliJ IDEA
1. Open Project dari folder `LudoApp`
2. Tunggu Maven import dependencies selesai
3. Klik kanan `LudoEliteApp.java`
4. Pilih "Run 'LudoEliteApp.main()'"

### Eclipse
1. Import Project sebagai "Existing Maven Project"
2. Klik kanan project → Run As → Java Application
3. Pilih `LudoEliteApp` sebagai main class

## Troubleshooting

### Error: "JavaFX runtime components are missing"
**Solusi:**
1. Pastikan `pom.xml` memiliki JavaFX dependencies
2. Atau download JavaFX SDK manual dari: https://openjfx.io/
3. Add VM arguments saat run:
   ```
   --module-path "path/to/javafx-sdk/lib" --add-modules javafx.controls,javafx.fxml
   ```

### Error: "Cannot find symbol" saat compile
**Solusi:**
1. Clean project: `mvn clean`
2. Re-compile: `mvn compile`
3. Atau delete folder `target/` dan compile ulang

### Error: "Port already in use" atau backend tidak connect
**Catatan:**
- Game board UI bisa dijalankan standalone tanpa backend
- Fitur yang butuh backend (login, game management) akan show error
- WebSocket hooks sudah siap tapi perlu backend implementation

## Quick Start untuk Testing Game Board

Jika hanya ingin test game board tanpa login:

1. Temporary comment code di `LudoEliteApp.java`:
```java
@Override
public void start(Stage primaryStage) {
    ViewNavigator.init(primaryStage);
    // ViewNavigator.navigateToLogin();  // Comment ini
    ViewNavigator.navigateToLudoBoard();  // Add ini
    primaryStage.show();
}
```

2. Run aplikasi
3. Game board akan langsung muncul

## File Structure Setelah Compile

```
LudoApp/
├── src/
├── target/
│   ├── classes/
│   │   └── com/ludoelite/  ← Compiled .class files
│   └── ...
├── pom.xml
├── compile.bat
└── run.bat
```

## Dependencies (dari pom.xml)

Pastikan pom.xml include:
- JavaFX Controls
- JavaFX FXML
- Jackson (untuk JSON parsing)
- HTTP Client (untuk API calls)

---

**Happy Coding! 🚀**
