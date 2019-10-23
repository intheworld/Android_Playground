package win.intheworld.treenodedb.data;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by swliu on 18-12-28.
 * liushuwei@xiaomi.com
 */

public class Node {

    public String name;

    public List<Node> subNodes;

    public Node() {
        this.subNodes = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Node> getSubNodes() {
        return subNodes;
    }

    public void setSubNodes(List<Node> subNodes) {
        this.subNodes = subNodes;
    }

}
