public class Card {
    private final String name;
    private final int A_init;
    private final int H_init;
    private int A_base;
    private int H_base;
    private int A_cur;
    private int H_cur;
    private int order;


    public Card(String card_name, int attack_init, int health_init, int entry_order) {
        this.name = card_name;
        this.A_init = attack_init;
        this.H_init = health_init;
        this.A_base= attack_init;
        this.H_base= health_init;
        this.A_cur= attack_init;
        this.H_cur= health_init;
        this.order=entry_order;
    }

    public int getAInit(){
        return A_init;
    }
    public int getABase(){
        return A_base;
    }
    public int getACur(){
        return A_cur;
    }
    public int getHInit(){
        return H_init;
    }
    public int getHBase(){
        return H_base;
    }
    public int getHCur(){
        return H_cur;
    }
    public int getOrder(){
        return order;
    }
    public String getName(){
        return name;
    }

    public void setABase(int attack_base){
        A_base=attack_base;
    }
    public void setACur(int attack_cur){
        A_cur= attack_cur;
    }
    public void setHBase(int health_base){
        H_base=health_base;
    }
    public void setHCur(int health_cur){
        H_cur= health_cur;
    }
    public void setOrder(int order){
        this.order=order;
    }
}
