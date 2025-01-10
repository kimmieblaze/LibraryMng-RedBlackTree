public class RedBlackTree<K extends Comparable<K>, V> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private class Node {
        K key;
        V value;
        Node left, right, parent;
        boolean color;

        Node(K key, V value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node root;

    // In-order traversal for displaying the tree
    public void inOrderTraversal() {
        inOrderHelper(root);
    }

    private void inOrderHelper(Node node) {
        if (node != null) {
            inOrderHelper(node.left);
            System.out.println("Key: " + node.key + ", Value: " + node.value + ", Color: " + (node.color ? "Red" : "Black"));
            inOrderHelper(node.right);
        }
    }

    // Insert a new key-value pair into the red-black tree
    public void insert(K key, V value) {
        Node newNode = new Node(key, value, RED, null);
        if (root == null) {
            root = newNode;
            root.color = BLACK;
        } else {
            Node current = root;
            Node parent = null;

            while (current != null) {
                parent = current;
                if (key.compareTo(current.key) < 0) {
                    current = current.left;
                } else if (key.compareTo(current.key) > 0) {
                    current = current.right;
                } else {
                    current.value = value; // Update value if key already exists
                    return;
                }
            }

            newNode.parent = parent;
            if (key.compareTo(parent.key) < 0) {
                parent.left = newNode;
            } else {
                parent.right = newNode;
            }

            fixAfterInsert(newNode);
        }
    }

    // Fix tree balance after insertion
    private void fixAfterInsert(Node node) {
        while (node != null && node != root && node.parent.color == RED) {
            if (node.parent == node.parent.parent.left) {
                Node uncle = node.parent.parent.right;
                if (uncle != null && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Node uncle = node.parent.parent.left;
                if (uncle != null && uncle.color == RED) {
                    node.parent.color = BLACK;
                    uncle.color = BLACK;
                    node.parent.parent.color = RED;
                    node = node.parent.parent;
                } else {
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    node.parent.color = BLACK;
                    node.parent.parent.color = RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = BLACK;
    }

    private void rotateLeft(Node node) {
        Node temp = node.right;
        node.right = temp.left;
        if (temp.left != null) {
            temp.left.parent = node;
        }
        temp.parent = node.parent;
        if (node.parent == null) {
            root = temp;
        } else if (node == node.parent.left) {
            node.parent.left = temp;
        } else {
            node.parent.right = temp;
        }
        temp.left = node;
        node.parent = temp;
    }

    private void rotateRight(Node node) {
        Node temp = node.left;
        node.left = temp.right;
        if (temp.right != null) {
            temp.right.parent = node;
        }
        temp.parent = node.parent;
        if (node.parent == null) {
            root = temp;
        } else if (node == node.parent.right) {
            node.parent.right = temp;
        } else {
            node.parent.left = temp;
        }
        temp.right = node;
        node.parent = temp;
    }

    // Public method to delete a key
    public void delete(K key) {
        Node node = getNode(key);
        if (node != null) {
            deleteNode(node);
        }
    }

    private void deleteNode(Node node) {
        Node toDelete = node;
        Node replacement;
        boolean originalColor = toDelete.color;

        if (node.left == null) {
            replacement = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            replacement = node.left;
            transplant(node, node.left);
        } else {
            toDelete = minimum(node.right);
            originalColor = toDelete.color;
            replacement = toDelete.right;

            if (toDelete.parent == node) {
                if (replacement != null) {
                    replacement.parent = toDelete;
                }
            } else {
                transplant(toDelete, toDelete.right);
                toDelete.right = node.right;
                toDelete.right.parent = toDelete;
            }

            transplant(node, toDelete);
            toDelete.left = node.left;
            toDelete.left.parent = toDelete;
            toDelete.color = node.color;
        }

        if (originalColor == BLACK) {
            fixAfterDelete(replacement);
        }
    }

    private void fixAfterDelete(Node node) {
        while (node != root && (node == null || node.color == BLACK)) {
            if (node == node.parent.left) {
                Node sibling = node.parent.right;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                if ((sibling.left == null || sibling.left.color == BLACK) &&
                    (sibling.right == null || sibling.right.color == BLACK)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.right == null || sibling.right.color == BLACK) {
                        sibling.left.color = BLACK;
                        sibling.color = RED;
                        rotateRight(sibling);
                        sibling = node.parent.right;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    if (sibling.right != null) sibling.right.color = BLACK;
                    rotateLeft(node.parent);
                    node = root;
                }
            } else {
                Node sibling = node.parent.left;
                if (sibling.color == RED) {
                    sibling.color = BLACK;
                    node.parent.color = RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                if ((sibling.right == null || sibling.right.color == BLACK) &&
                    (sibling.left == null || sibling.left.color == BLACK)) {
                    sibling.color = RED;
                    node = node.parent;
                } else {
                    if (sibling.left == null || sibling.left.color == BLACK) {
                        sibling.right.color = BLACK;
                        sibling.color = RED;
                        rotateLeft(sibling);
                        sibling = node.parent.left;
                    }
                    sibling.color = node.parent.color;
                    node.parent.color = BLACK;
                    if (sibling.left != null) sibling.left.color = BLACK;
                    rotateRight(node.parent);
                    node = root;
                }
            }
        }
        if (node != null) node.color = BLACK;
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    private Node getNode(K key) {
        Node current = root;
        while (current != null) {
            int cmp = key.compareTo(current.key);
            if (cmp < 0) {
                current = current.left;
            } else if (cmp > 0) {
                current = current.right;
            } else {
                return current;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        RedBlackTree<Integer, String> tree = new RedBlackTree<>();
        tree.insert(10, "Book A");
        tree.insert(20, "Book B");
        tree.insert(15, "Book C");
        tree.insert(25, "Book D");

        System.out.println("In-Order Traversal of Red-Black Tree:");
        tree.inOrderTraversal();

        System.out.println("\nDeleting key 15...");
        tree.delete(15);
        tree.inOrderTraversal();
    }
}
