

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;



public class Node implements Serializable{
	public Node parent;//���ڵ�
    public List<Node> children = new ArrayList<Node>();//�ӽڵ�    
	public Object ob;
    public int icon = -1;//�Ƿ���ʾСͼ��,-1��ʾ����ͼ��
    public boolean isExpanded = false;//�Ƿ���չ��״̬
    public boolean isSelect = false;   
    
    
    
    public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	/**
     * Node���캯��
     * @param text �ڵ���ʾ������
     * @param value �ڵ��ֵ
     */
    public Node(Object ob){
    	this.ob= ob;    	
    }
    
    /**
     * ���ø��ڵ�
     * @param node
     */
    public void setParent(Node node){
    	this.parent = node;
    }
    /**
     * ��ø��ڵ�
     * @return
     */
    public Node getParent(){
    	return this.parent;
    }    
    
    public Object getOb() {
		return ob;
	}

	public void setOb(Object ob) {
		this.ob = ob;
	}

	/**
     * ���ýڵ�ͼ���ļ�
     * @param icon
     */
    public void setIcon(int icon){
    	this.icon = icon;
    }
    /**
     * ���ͼ���ļ�
     * @return
     */
    public int getIcon(){
    	return icon;
    }
    /**
     * �Ƿ��ڵ�
     * @return
     */
    public boolean isRoot(){
    	return parent==null?true:false;
    }
    /**
     * ����ӽڵ�
     * @return
     */
    
    public Object getUserObject() {
        return ob;
    }
    public Node getNextNode() {
        if (getChildCount() == 0) {
            // No children, so look for nextSibling
            Node nextSibling = getNextSibling();

            if (nextSibling == null) {
                Node aNode = (Node)getParent();

                do {
                    if (aNode == null) {
                        return null;
                    }

                    nextSibling = aNode.getNextSibling();
                    if (nextSibling != null) {
                        return nextSibling;
                    }

                    aNode = (Node)aNode.getParent();
                } while(true);
            } else {
                return nextSibling;
            }
        } else {
            return (Node)getChildAt(0);
        }
    }
    
    public Node getNextSibling() {
        Node retval;

        Node myParent = (Node)getParent();

        if (myParent == null) {
            retval = null;
        } else {
            retval = (Node)myParent.getChildAfter(this);      // linear search
        }

        if (retval != null && !isNodeSibling(retval)) {
            throw new Error("child of parent is not a sibling");
        }

        return retval;
    }
    
    public Node getChildAfter(Node aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }

        int index = getIndex(aChild);           // linear search

        if (index == -1) {
            throw new IllegalArgumentException("node is not a child");
        }

        if (index < getChildCount() - 1) {
            return getChildAt(index + 1);
        } else {
            return null;
        }
    }
    
    public int getIndex(Node aChild) {
        if (aChild == null) {
            throw new IllegalArgumentException("argument is null");
        }

        if (!isNodeChild(aChild)) {
            return -1;
        }
        return children.indexOf(aChild);        // linear search
    }
    
    public boolean isNodeSibling(Node anotherNode) {
        boolean retval;

        if (anotherNode == null) {
            retval = false;
        } else if (anotherNode == this) {
            retval = true;
        } else {
            Node  myParent = getParent();
            retval = (myParent != null && myParent == anotherNode.getParent());

            if (retval && !((Node)getParent())
                           .isNodeChild(anotherNode)) {
                throw new Error("sibling has different parent");
            }
        }
        return retval;
    }
    
    public boolean isNodeChild(Node aNode) {
        boolean retval;

        if (aNode == null) {
            retval = false;
        } else {
            if (getChildCount() == 0) {
                retval = false;
            } else {
                retval = (aNode.getParent() == this);
            }
        }

        return retval;
    }
    
    public Node getLastChildren(){
    	if (getChildCount() == 0) {
            throw new NoSuchElementException("node has no children");
        }
        return getChildAt(getChildCount()-1);
    }
    
    private Node getChildAt(int i) {
    	if (children == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        return children.get(i);
		
	}

	public List<Node> getChildren(){
    	return this.children;
    }
    
    public void setChildren(List<Node> children) {
		this.children = children;
	}
    /**
     * ����ӽڵ�
     * @param node
     */
    public void add(Node node){
    	if(node != null ){
    		node.setParent(this);
    		children.add(node);
    	}
            
    	/*
    	if(!children.contains(node)){
    		children.add(node);
    	}
    	*/
    }
    private int getChildCount() {
    	if (children == null) {
            return 0;
        } else {
            return children.size();
        }
	}

	/**
     * ��������ӽڵ�
     */
    public void clear(){
    	children.clear();
    }
    /**
     * ɾ��һ���ӽڵ�
     * @param node
     */
    public void remove(Node node){
    	if(children.contains(node)){
    		children.remove(node);
    	}
    }
    /**
     * ɾ��ָ��λ�õ��ӽڵ�
     * @param location
     */
    public void remove(int location){
    	if(location<children.size() && location>=0)
    		children.remove(location);
    }
    /**
     * ��ýڵ�ļ���,��ڵ�Ϊ0
     * @return
     */
    public int getLevel(){
    	return parent==null?0:parent.getLevel()+1;
    }
    /**
     * �Ƿ�Ҷ�ڵ�,��û���ӽڵ�Ľڵ�
     * @return
     */
    public boolean isLeaf(){
    	return children.size()<1?true:false;
    }
    /**
    * ��ǰ�ڵ��Ƿ���չ��״̬ 
    * @return
    */
    public boolean isExpanded(){
        return isExpanded;
    }
    /**
     * ���ýڵ�չ��״̬
     * @return
     */
    public void setExpanded(boolean isExpanded){
    	 this.isExpanded =  isExpanded;
    }

//	public String getTag(int i) {
//		Class<? extends Object> t = getChildren().get(i).getOb().getClass();
//		Object item = getChildren().get(i).getOb();
//		if(t.equals(Group.class)){
//			return ((Group)(item)).getGroupName();
//		}
//		if(t.equals(User.class)){
//			return ((User)(item)).getDispName();
//		}
//		if(t.equals(VMFile.class)){
//			return ((VMFile)(item)).getFileName();
//		}
//		return "";
//	}
 
}