// generated with ast extension for cup
// version 0.8
// 20/7/2022 3:20:57


package rs.ac.bg.etf.pp1.ast;

public class IdentDesignatorElem extends DesignatorElem {

    private String designatorElemName;

    public IdentDesignatorElem (String designatorElemName) {
        this.designatorElemName=designatorElemName;
    }

    public String getDesignatorElemName() {
        return designatorElemName;
    }

    public void setDesignatorElemName(String designatorElemName) {
        this.designatorElemName=designatorElemName;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public void childrenAccept(Visitor visitor) {
    }

    public void traverseTopDown(Visitor visitor) {
        accept(visitor);
    }

    public void traverseBottomUp(Visitor visitor) {
        accept(visitor);
    }

    public String toString(String tab) {
        StringBuffer buffer=new StringBuffer();
        buffer.append(tab);
        buffer.append("IdentDesignatorElem(\n");

        buffer.append(" "+tab+designatorElemName);
        buffer.append("\n");

        buffer.append(tab);
        buffer.append(") [IdentDesignatorElem]");
        return buffer.toString();
    }
}
