public class Deck {
    private Node root;
    private int cardCount;

    private int compareCards(Card c1,Card c2){
        int acur1= c1.getACur();
        int acur2= c2.getACur();
        if (acur1 != acur2){
            return acur1-acur2;
        }

        int hcur1= c1.getHCur();
        int hcur2= c2.getHCur();
        if (hcur1 != hcur2){
            return hcur1-hcur2;
        }

        return c1.getOrder() - c2.getOrder();
    }

    private int getHeight(Node n){
        if (n==null){
            return 0;
        }
        else return n.getHeight();
    }

    // İSİM DÜZELTİLDİ: getBalancedFactor yerine getBalanceFactor kullanılıyor.
    private int getBalanceFactor(Node n){
        if (n==null){
            return 0;
        }
        return getHeight(n.getLeft())-getHeight(n.getRight());
    }

    private Node rotateRight(Node y){
        Node x= y.getLeft();
        Node t2= x.getRight();
        x.setRight(y);
        y.setLeft(t2);
        y.setHeight(Math.max(getHeight(y.getRight()), getHeight(y.getLeft())) +1);
        x.setHeight(Math.max(getHeight(x.getRight()), getHeight(x.getLeft())) +1);
        return x;
    }

    private Node rotateLeft(Node x){
        Node y= x.getRight();
        Node t2= y.getLeft();
        y.setLeft(x);
        x.setRight(t2);
        x.setHeight(Math.max(getHeight(x.getRight()), getHeight(x.getLeft())) +1);
        y.setHeight(Math.max(getHeight(y.getRight()), getHeight(y.getLeft())) +1);
        return y;
    }

    private Node insertRecursive(Node node, Card card){
        if (node==null){
            cardCount++;
            return new Node(card);
        }
        int compareResult = compareCards(card, node.getData());
        if (compareResult<0){
            node.setLeft(insertRecursive(node.getLeft(), card));
        }else if (compareResult>0){
            node.setRight(insertRecursive(node.getRight(),card));
        }else{
            return node;
        }

        node.setHeight(Math.max(getHeight(node.getLeft()), getHeight(node.getRight())) +1);

        int balance=getBalanceFactor(node); // DÜZELTME
        if (balance>=2){
            if (getBalanceFactor(node.getLeft())>=0) { // DÜZELTME
                return rotateRight(node);
            }else{
                node.setLeft(rotateLeft(node.getLeft()));
                return rotateRight(node);
            }
        }else if (balance<=-2){
            if (getBalanceFactor(node.getRight())<=0){ // DÜZELTME
                return rotateLeft(node);
            }else{
                node.setRight(rotateRight(node.getRight()));
                return rotateLeft(node);
            }
        }
        return node;
    }

    public void insert(Card card){
        this.root = insertRecursive(this.root, card);
    }

    public void delete(Card cardToDelete) {
        this.root = deleteRecursive(this.root, cardToDelete);
    }

    private Node deleteRecursive(Node node, Card cardToDelete){
        if (node == null) return null;

        int compareResult = compareCards(cardToDelete, node.getData());

        if (compareResult < 0) {
            node.setLeft(deleteRecursive(node.getLeft(), cardToDelete));
        } else if (compareResult > 0) {
            node.setRight(deleteRecursive(node.getRight(), cardToDelete));
        } else {
            this.cardCount--;

            if (node.getLeft() == null || node.getRight() == null) {
                Node temp = (node.getLeft() != null) ? node.getLeft() : node.getRight();
                return temp;
            }

            Node successor = node.getRight();
            while (successor.getLeft() != null) {
                successor = successor.getLeft();
            }

            // KRİTİK DÜZELTME: Veri ataması yerine sadece kart istatistikleri kopyalanır.
            // Bu, 'entry_order'ı korur.
            node.getData().setABase(successor.getData().getABase());
            node.getData().setHBase(successor.getData().getHBase());
            node.getData().setACur(successor.getData().getACur());
            node.getData().setHCur(successor.getData().getHCur());
            node.getData().setOrder(successor.getData().getOrder()); // Successor'ın sırası atanır

            // node.setData(successor.getData()); // ARTIK KULLANILMIYOR

            // Successor'ı silmek için rekürsif çağrı yapılır.
            node.setRight(deleteRecursive(node.getRight(), successor.getData()));
        }

        if (node == null) return null;

        node.setHeight(Math.max(getHeight(node.getLeft()), getHeight(node.getRight())) + 1);

        int balance = getBalanceFactor(node); // DÜZELTME

        if (balance > 1) {
            if (getBalanceFactor(node.getLeft()) >= 0) { // DÜZELTME
                return rotateRight(node);
            } else {
                node.setLeft(rotateLeft(node.getLeft()));
                return rotateRight(node);
            }
        }

        if (balance < -1) {
            if (getBalanceFactor(node.getRight()) <= 0) { // DÜZELTME
                return rotateLeft(node);
            } else {
                node.setRight(rotateRight(node.getRight()));
                return rotateLeft(node);
            }
        }
        return node;
    }


    public Card findBestStealCandidate(int attackLimit, int healthLimit) {
        return stealRecursive(this.root, null, attackLimit, healthLimit);
    }

    private Card stealRecursive(Node node, Card bestCandidate, int attackLimit, int healthLimit) {
        if (node == null) {
            return bestCandidate;
        }

        Card currentCard = node.getData();
        int aCur = currentCard.getACur();
        int hCur = currentCard.getHCur();

        if (aCur > attackLimit && hCur > healthLimit) {
            if (bestCandidate == null || compareCards(currentCard, bestCandidate) < 0) {
                bestCandidate = currentCard;
            }
        }

        if (aCur <= attackLimit) {
            return stealRecursive(node.getRight(), bestCandidate, attackLimit, healthLimit);
        }

        Card candidateFromLeft = stealRecursive(node.getLeft(), bestCandidate, attackLimit, healthLimit);

        if (candidateFromLeft != null && (bestCandidate == null || compareCards(candidateFromLeft, bestCandidate) < 0)) {
            bestCandidate = candidateFromLeft;
        }

        if (aCur > attackLimit) {
            bestCandidate = stealRecursive(node.getRight(), bestCandidate, attackLimit, healthLimit);
        }

        return bestCandidate;
    }

    public Card findOptimalBattleCard(int strangerAttack, int strangerHealth) {
        Card candidate;

        candidate = findP1Recursive(this.root, null, strangerAttack, strangerHealth);
        if (candidate != null) return candidate;

        candidate = findP2Recursive(this.root, null, strangerAttack, strangerHealth);
        if (candidate != null) return candidate;

        candidate = findP3Recursive(this.root, null, strangerAttack, strangerHealth);
        if (candidate != null) return candidate;

        candidate = findP4Recursive(this.root, null, strangerAttack, strangerHealth);

        return candidate;
    }

    private Card findP1Recursive(Node node, Card bestCandidate, int sa, int sh) {
        if (node == null) {
            return bestCandidate;
        }

        Card currentCard = node.getData();
        int hCur = currentCard.getHCur();
        int aCur = currentCard.getACur();

        boolean survives = hCur > sa;
        boolean kills = aCur >= sh;

        if (aCur >= sh) {
            bestCandidate = findP1Recursive(node.getLeft(), bestCandidate, sa, sh);
        }

        if (survives && kills) {
            if (bestCandidate == null || compareCards(currentCard, bestCandidate) < 0) {
                bestCandidate = currentCard;
            }
        }

        if (aCur > sh) {
            bestCandidate = findP1Recursive(node.getRight(), bestCandidate, sa, sh);
        }

        return bestCandidate;
    }

    private Card findP2Recursive(Node node, Card bestCandidate, int sa, int sh) {
        if (node == null) return bestCandidate;

        Card currentCard = node.getData();
        int hCur = currentCard.getHCur();
        int aCur = currentCard.getACur();

        boolean survives = hCur > sa;
        boolean kills = aCur >= sh;

        if (survives && !kills) {
            if (bestCandidate == null) {
                bestCandidate = currentCard;
            } else if (aCur > bestCandidate.getACur()) {
                bestCandidate = currentCard;
            } else if (aCur == bestCandidate.getACur() && compareCards(currentCard, bestCandidate) < 0) {
                bestCandidate = currentCard;
            }
        }

        if (bestCandidate == null || aCur > bestCandidate.getACur()) {
            bestCandidate = findP2Recursive(node.getRight(), bestCandidate, sa, sh);
        }

        if (bestCandidate != null && aCur == bestCandidate.getACur()) {
            bestCandidate = findP2Recursive(node.getLeft(), bestCandidate, sa, sh);
        }

        return bestCandidate;
    }

    private Card findP3Recursive(Node node, Card bestCandidate, int sa, int sh) {
        if (node == null) return bestCandidate;

        Card currentCard = node.getData();
        int hCur = currentCard.getHCur();
        int aCur = currentCard.getACur();

        boolean survives = hCur > sa;
        boolean kills = aCur >= sh;

        if (!survives && kills) {
            if (bestCandidate == null || compareCards(currentCard, bestCandidate) < 0) {
                bestCandidate = currentCard;
            }
        }

        if (aCur >= sh) {
            bestCandidate = findP3Recursive(node.getLeft(), bestCandidate, sa, sh);
        }

        if (aCur > sh) {
            bestCandidate = findP3Recursive(node.getRight(), bestCandidate, sa, sh);
        }

        return bestCandidate;
    }

    private Card findP4Recursive(Node node, Card bestCandidate, int sa, int sh) {
        if (node == null) return bestCandidate;

        Card currentCard = node.getData();
        int aCur = currentCard.getACur();

        if (bestCandidate == null) {
            bestCandidate = currentCard;
        } else if (aCur > bestCandidate.getACur()) {
            bestCandidate = currentCard;
        } else if (aCur == bestCandidate.getACur() && compareCards(currentCard, bestCandidate) < 0) {
            bestCandidate = currentCard;
        }

        if (currentCard.getACur() > (bestCandidate == null ? -1 : bestCandidate.getACur())) {
            bestCandidate = findP4Recursive(node.getRight(), bestCandidate, sa, sh);
        }

        if (bestCandidate != null && aCur == bestCandidate.getACur()) {
            bestCandidate = findP4Recursive(node.getLeft(), bestCandidate, sa, sh);
        }

        return bestCandidate;
    }

    public int getCardCount() {
        return this.cardCount;
    }
}