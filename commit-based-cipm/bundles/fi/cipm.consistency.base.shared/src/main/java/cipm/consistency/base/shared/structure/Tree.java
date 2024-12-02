package cipm.consistency.base.shared.structure;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private TreeNode<T> root;

    public Tree(T rootData) {
        root = new TreeNode<T>();
        root.setData(rootData);
    }

    public Tree(TreeNode<T> root) {
        this.root = root;
        this.root.setParent(null);
    }

    public TreeNode<T> getRoot() {
        return root;
    }

    public static class TreeNode<T> {
        private T data;
        private TreeNode<T> parent;
        private List<TreeNode<T>> children;

        public TreeNode() {
            this.children = new ArrayList<>();
        }

        public TreeNode<T> addChildren(T data) {
            TreeNode<T> child = new TreeNode<T>();
            child.setData(data);
            child.setParent(this);
            this.children.add(child);

            return child;
        }

        public void addChildren(TreeNode<T> child) {
            child.setParent(this);
            this.children.add(child);
        }

        @SuppressWarnings("rawtypes")
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            TreeNode other = (TreeNode) obj;
            if (children == null) {
                if (other.children != null)
                    return false;
            } else if (!children.equals(other.children))
                return false;
            if (data == null) {
                if (other.data != null)
                    return false;
            } else if (!data.equals(other.data))
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((children == null) ? 0 : children.hashCode());
            result = prime * result + ((data == null) ? 0 : data.hashCode());
            return result;
        }

		public T getData() {
			return data;
		}

		public void setData(T data) {
			this.data = data;
		}

		public TreeNode<T> getParent() {
			return parent;
		}

		public void setParent(TreeNode<T> parent) {
			this.parent = parent;
		}

		public List<TreeNode<T>> getChildren() {
			return new ArrayList<>(children);
		}

		public void setChildren(List<TreeNode<T>> children) {
			this.children.clear();
			this.children.addAll(children);
		}
    }
}