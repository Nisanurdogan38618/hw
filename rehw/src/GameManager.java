public class GameManager {
    private Deck deck;
    private int survivorPoints;
    private int strangerPoints;
    private int entryCounter;

    public GameManager() {
        this.deck = new Deck();
        this.survivorPoints = 0;
        this.strangerPoints = 0;
        this.entryCounter = 1;
    }

    public String handleDrawCard(String name, int att, int hp) {
        Card newCard = new Card(name, att, hp, this.entryCounter);
        this.entryCounter++;
        deck.insert(newCard);
        return "Added " + name + " to the deck\n";
    }

    public String handleDeckCount() {
        int count = deck.getCardCount();
        return "Number of cards in the deck: " + count + "\n";
    }

    public String handleFindWinning() {
        if (this.survivorPoints >= this.strangerPoints) {
            return "The Survivor, Score: " + this.survivorPoints + "\n";
        } else {
            return "The Stranger, Score: " + this.strangerPoints + "\n";
        }
    }

    public String handleStealCard(int attackLimit, int healthLimit) {
        Card stolenCard = deck.findBestStealCandidate(attackLimit, healthLimit);

        if (stolenCard == null) {
            return "No card to steal\n";
        } else {
            deck.delete(stolenCard);
            return "The Stranger stole the card: " + stolenCard.getName() + "\n";
        }
    }

    public String handleBattle(int strangerAttack, int strangerHealth, int healPoolAmount) {
        // 1. Adayı bul
        Card playedCard = deck.findOptimalBattleCard(strangerAttack, strangerHealth);

        // --- Durum 3: Kart Yoksa ---
        if (playedCard == null) {
            updateScores("Stranger", 2);
            return "No cards to play, 0 cards revived\n";
        }

        String cardName = playedCard.getName();

        // Savaş öncesi statlar
        int H_cur_initial = playedCard.getHCur();
        int A_cur_initial = playedCard.getACur();
        int H_base = playedCard.getHBase();

        // 2. Savaş Çözümlemesi
        int H_cur_final = H_cur_initial - strangerAttack;
        int H_stranger_final = strangerHealth - A_cur_initial;

        // Skor Hesaplama
        int survivorScore = 0;
        int strangerScore = 0;

        // A. Kart Ölümü/Hasarı Kontrolü
        if (H_cur_final <= 0) {
            strangerScore += 2;
        } else if (H_cur_final > 0) { // Kart hayatta kaldıysa
            if (H_cur_final < H_base) { // Hasar aldıysa
                strangerScore += 1;
            }
        }

        // B. Stranger Kartının Ölümü/Hasarı Kontrolü
        if (H_stranger_final <= 0) {
            survivorScore += 2;
        } else { // Stranger hayatta kaldıysa
            if (H_stranger_final < strangerHealth) { // Stranger hasar aldıysa
                survivorScore += 1;
            }
        }

        updateScores("Survivor", survivorScore);
        updateScores("Stranger", strangerScore);

        // 3. AVL Ağacını ve Kartı Güncelleme
        String outputMessage;

        // Kartın Önceliğini Bul (Çıktı için)
        boolean survives_initial = H_cur_initial > strangerAttack;
        boolean kills_initial = A_cur_initial >= strangerHealth;
        int priority = 0;

        if (survives_initial && kills_initial) priority = 1;
        else if (survives_initial && !kills_initial) priority = 2;
        else if (!survives_initial && kills_initial) priority = 3;
        else priority = 4;


        if (H_cur_final <= 0) {
            // Durum 1: Kart öldü
            deck.delete(playedCard);
            outputMessage = String.format("Found with priority %d, Survivor plays %s, the played card is discarded, 0 cards revived\n", priority, cardName);
        } else {
            // Durum 2: Kart hayatta kaldı

            // 3a. Yeni H_cur'ı ayarla
            playedCard.setHCur(H_cur_final);

            // 3b. Yeni A_cur'ı hesapla ve ayarla (Attack Reduction)
            int A_base = playedCard.getABase();
            int H_cur_updated = playedCard.getHCur();

            // KİLİT DÜZELTME: Tam sayı bölmesinin Floor/Integer sonucunu kullan.
            // long kullanmak zorunlu, aksi takdirde (A_base * H_cur_updated) çarpımı int sınırlarını aşabilir.
            long rawResult = (long) A_base * H_cur_updated;
            int newACur = (int) (rawResult / H_base); // Java'da long / int tam sayıya yuvarlar (floor)

            newACur = Math.max(1, newACur);
            playedCard.setACur(newACur);

            this.entryCounter++;
            playedCard.setOrder(this.entryCounter);

            // 3c. AVL Güncellemesi (A_cur değiştiği için sil/ekle)
            deck.delete(playedCard);
            deck.insert(playedCard);

            outputMessage = String.format("Found with priority %d, Survivor plays %s, the played card returned to deck, 0 cards revived\n", priority, cardName);
        }

        return outputMessage;
    }

    private void updateScores(String winner, int points) {
        if (winner.equals("Survivor")) {
            this.survivorPoints += points;
        } else if (winner.equals("Stranger")) {
            this.strangerPoints += points;
        }
    }
}
