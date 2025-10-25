public class Node {
    private Card data;
    private Node right;
    private Node left;
    private int height;

    public Node(Card card_data) {
        this.data = card_data;
        this.right=null;
        this.left=null;
        this.height=1;
    }

    public Card getData(){
        return data;
    }
    public Node getRight(){
        return right;
    }
    public Node getLeft(){
        return left;
    }
    public int getHeight(){
        return height;
    }

    public void setData(Card data){
        this.data=data;
    }
    public void setRight(Node right){
        this.right=right;
    }
    public void setLeft(Node left){
        this.left=left;
    }
    public void setHeight(int height){
        this.height=height;
    }
}

