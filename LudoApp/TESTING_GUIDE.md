# 🧪 Testing Guide - Ludo Elite

## Quick Test Checklist

### Pre-Launch Tests

#### 1. Compilation Test
```bash
# Pastikan compile tanpa error
mvn clean compile

# Expected: BUILD SUCCESS
```

#### 2. Launch Test
```bash
mvn javafx:run

# Expected: Login screen muncul
```

---

## Functional Testing

### Test 1: Navigation to Game Board ✅

**Steps:**
1. Login dengan user yang valid
2. Click "Play Game" dari dashboard
3. Game board harus muncul

**Expected:**
- Canvas 600x600 pixel terlihat
- 4 player info panels terlihat
- Dice control area terlihat
- Papan Ludo dengan 4 warna base terlihat

**Status:** [ ] Pass [ ] Fail

---

### Test 2: Dice Rolling ✅

**Steps:**
1. Click tombol "ROLL DICE"
2. Observe dice animation
3. Check dice result display

**Expected:**
- Animasi dice rolling (angka berubah-ubah)
- Final value ditampilkan (1-6)
- Tombol roll disabled setelah roll

**Test Cases:**
- Roll normal (1-5): [ ] Pass
- Roll 6: [ ] Extra turn [ ] Pass
- Roll 6 tiga kali berturut-turut: [ ] Skip turn [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 3: Piece Movement from Base ✅

**Steps:**
1. Roll dice sampai dapat 6
2. Click pada bidak di base
3. Observe piece movement

**Expected:**
- Bidak keluar dari base slot
- Bidak muncul di starting position
- Turn berlanjut (karena roll 6)

**Test Cases:**
- Roll 6, click piece: [ ] Piece exits [ ] Pass
- Roll 1-5, click piece: [ ] Piece stays [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 4: Normal Piece Movement ✅

**Steps:**
1. Punya bidak di track
2. Roll dice
3. Click bidak yang valid
4. Observe movement

**Expected:**
- Bidak bergerak sejumlah dice value
- Position update terlihat di canvas
- Turn pindah ke player berikutnya (jika bukan 6)

**Test Cases:**
- Roll 3, move 3 steps: [ ] Pass
- Roll 5, move 5 steps: [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 5: Capture Mechanism ✅

**Steps:**
1. Setup: Punya bidak di track
2. Opponent punya bidak di posisi yang bisa dicapai
3. Roll dice untuk land di posisi opponent
4. Observe capture

**Expected:**
- Opponent piece kembali ke base
- Your piece occupy the position
- No capture di safe zone (★)

**Test Cases:**
- Capture di normal tile: [ ] Pass
- Cannot capture di safe zone: [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 6: Safe Zones ✅

**Steps:**
1. Move piece ke safe zone (★)
2. Opponent coba land di posisi yang sama
3. Observe behavior

**Expected:**
- Piece di safe zone tidak bisa dicapture
- Multiple pieces bisa ada di safe zone

**Safe Zone Positions to Test:**
- Position 0, 8, 13, 21, 26, 34, 39, 47

**Status:** [ ] Pass [ ] Fail

---

### Test 7: Home Lane Entry ✅

**Steps:**
1. Move piece mendekati home entry position
2. Roll dice untuk land exactly di home entry
3. Observe piece entering colored home lane

**Expected:**
- Piece masuk ke home lane berwarna
- Piece position update ke home lane track

**Home Entry Positions:**
- Red: 50 [ ] Pass
- Green: 11 [ ] Pass
- Yellow: 24 [ ] Pass
- Blue: 37 [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 8: Finish Mechanism ✅

**Steps:**
1. Punya piece di home lane
2. Roll exact count untuk reach center
3. Observe finish

**Expected:**
- Piece masuk center dengan exact count
- Over-roll (more than needed) tidak allowed

**Test Cases:**
- 3 steps from finish, roll 3: [ ] Finish [ ] Pass
- 3 steps from finish, roll 5: [ ] Cannot move [ ] Pass

**Status:** [ ] Pass [ ] Fail

---

### Test 9: Win Condition ✅

**Steps:**
1. Move all 4 pieces ke finish
2. Observe win announcement

**Expected:**
- Alert dialog muncul
- Winner name displayed
- Game status update

**Status:** [ ] Pass [ ] Fail

---

### Test 10: Turn Management ✅

**Steps:**
1. Play multiple turns
2. Observe turn order
3. Check player highlighting

**Expected:**
- Turn order: Red → Green → Yellow → Blue → Red
- Current player highlighted (border + background)
- Roll button only enabled untuk current player

**Status:** [ ] Pass [ ] Fail

---

### Test 11: Three Consecutive Sixes ✅

**Steps:**
1. Roll 6 (first time)
2. Roll 6 (second time)
3. Roll 6 (third time)
4. Observe skip turn

**Expected:**
- After third 6, turn skipped automatically
- Message: "Three 6s in a row! Turn skipped."
- Next player's turn starts

**Status:** [ ] Pass [ ] Fail

---

### Test 12: No Valid Moves ✅

**Steps:**
1. All pieces di base
2. Roll 1-5 (not 6)
3. Observe auto-skip

**Expected:**
- Message: "No valid moves available!"
- Auto-skip turn after delay
- Next player's turn

**Status:** [ ] Pass [ ] Fail

---

## UI/UX Testing

### Test 13: Visual Elements ✅

**Checklist:**
- [ ] Board renders correctly
- [ ] All 4 bases visible dan berwarna
- [ ] Track positions jelas
- [ ] Safe zones marked dengan ★
- [ ] Home lanes berwarna sesuai player
- [ ] Center finish area visible
- [ ] Pieces rendered dengan benar (warna, border, shadow)

**Status:** [ ] Pass [ ] Fail

---

### Test 14: Responsiveness ✅

**Steps:**
1. Click buttons rapidly
2. Hover over pieces
3. Check animations

**Expected:**
- No lag atau freeze
- Animations smooth
- UI updates immediately

**Status:** [ ] Pass [ ] Fail

---

### Test 15: Theme Consistency ✅

**Checklist:**
- [ ] Dark theme consistent (background #0f172a)
- [ ] Gold accent visible (#f59e0b)
- [ ] Player colors match spec:
  - [ ] Red: #ef4444
  - [ ] Green: #22c55e
  - [ ] Yellow: #f59e0b
  - [ ] Blue: #3b82f6
- [ ] Font readable
- [ ] Buttons styled correctly

**Status:** [ ] Pass [ ] Fail

---

## OOP Testing (Code Review)

### Test 16: Encapsulation Verification ✅

**Check:**
```java
// All fields should be private
GamePiece piece = ...;
piece.pieceNumber = 10;  // Should be COMPILE ERROR

// Access hanya lewat getter/setter
int num = piece.getPieceNumber();  // Should be OK
```

**Status:** [ ] Pass [ ] Fail

---

### Test 17: Inheritance Verification ✅

**Check:**
```java
// RedPiece should have all GamePiece methods
RedPiece red = new RedPiece(0);
boolean canMove = red.canMove();      // Inherited
red.sendToBase();                     // Inherited
red.render(gc, x, y, size);          // Overridden
```

**Status:** [ ] Pass [ ] Fail

---

### Test 18: Polymorphism Verification ✅

**Check:**
```java
// Should be able to store different types
List<GamePiece> pieces = new ArrayList<>();
pieces.add(new RedPiece(0));
pieces.add(new BluePiece(1));

// Should call correct render() based on type
for (GamePiece piece : pieces) {
    piece.render(gc, x, y, size);  // Polymorphic call
}
```

**Status:** [ ] Pass [ ] Fail

---

### Test 19: Abstraction Verification ✅

**Check:**
```java
// Should NOT be able to instantiate abstract class
GamePiece piece = new GamePiece(0, RED);  // Should be ERROR

// Must use concrete type
GamePiece piece = new RedPiece(0);  // Should be OK
```

**Status:** [ ] Pass [ ] Fail

---

## Performance Testing

### Test 20: Rendering Performance ✅

**Steps:**
1. Launch game
2. Play for 5 minutes
3. Move pieces rapidly
4. Observe frame rate

**Expected:**
- No visible lag
- Canvas updates smooth
- Memory usage stable

**Status:** [ ] Pass [ ] Fail

---

## Integration Testing (Future)

### Test 21: WebSocket Hooks (Placeholder)

**Verify hooks exist:**
```java
// In LudoBoardController.java
- [ ] onOpponentMoveReceived() method exists
- [ ] sendMoveToServer() method exists
- [ ] onDiceRollReceived() method exists
- [ ] sendDiceRollToServer() method exists
```

**Status:** [ ] Ready for integration

---

## Edge Cases Testing

### Test 22: Multiple Pieces Same Position ✅

**Steps:**
1. Move 2 pieces dari player yang sama ke posisi sama (safe zone)
2. Click untuk move salah satu
3. Observe behavior

**Expected:**
- Should be able to select correct piece
- Both pieces visible (stacked or offset)

**Status:** [ ] Pass [ ] Fail

---

### Test 23: Rapid Clicking ✅

**Steps:**
1. Roll dice
2. Click piece multiple times rapidly
3. Observe behavior

**Expected:**
- Piece moves only once
- No duplicate moves
- No errors

**Status:** [ ] Pass [ ] Fail

---

### Test 24: Invalid Move Attempts ✅

**Steps:**
1. Try to move opponent's piece
2. Try to move finished piece
3. Try to click before rolling

**Expected:**
- Error messages displayed
- No invalid moves executed
- Game state unchanged

**Status:** [ ] Pass [ ] Fail

---

## Regression Testing

### Test 25: Navigation ✅

**Steps:**
1. Play game
2. Click "Back to Dashboard"
3. Confirm leave
4. Return to game board

**Expected:**
- Game state reset
- New game starts fresh
- No memory leaks

**Status:** [ ] Pass [ ] Fail

---

## Test Results Summary

```
┌─────────────────────────────────────────┐
│         TEST RESULTS SUMMARY            │
├─────────────────────────────────────────┤
│ Functional Tests:    [ ] / 15          │
│ UI/UX Tests:         [ ] / 3           │
│ OOP Tests:           [ ] / 4           │
│ Performance Tests:   [ ] / 1           │
│ Edge Cases:          [ ] / 3           │
│ Regression Tests:    [ ] / 1           │
├─────────────────────────────────────────┤
│ TOTAL:               [ ] / 27          │
└─────────────────────────────────────────┘

Pass Rate: ____%

Status: [ ] READY [ ] NEEDS FIXES
```

---

## Bug Report Template

```
Bug ID: #___
Title: [Short description]

Steps to Reproduce:
1.
2.
3.

Expected Behavior:


Actual Behavior:


Severity: [ ] Critical [ ] High [ ] Medium [ ] Low

Screenshot/Video: [If applicable]

Tested By: ___________
Date: ___________
```

---

## Testing Notes

### Setup Before Testing
1. Clean compile: `mvn clean compile`
2. Fresh database (if backend connected)
3. Clear browser cache (if web version)

### During Testing
- Take screenshots of any bugs
- Note exact steps to reproduce
- Check console for error messages
- Monitor memory usage

### After Testing
- Fill out test results summary
- Report all bugs found
- Verify fixes with retest
- Sign off on test completion

---

**Happy Testing! 🧪**

Testing Completed By: __________________
Date: __________________
Sign: __________________
