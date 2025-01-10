import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

// Red-Black Tree node class
class RedBlackNode {
    String title;
    RedBlackNode left, right, parent;
    int color;  // 0 = Black, 1 = Red

    public RedBlackNode(String title) {
        this.title = title;
        this.left = this.right = this.parent = null;
        this.color = 1;  // New nodes are red
    }
}

// Red-Black Tree class
class RedBlackTree {
    private RedBlackNode root;
    private RedBlackNode TNULL;  // Sentinel node (null)

    public RedBlackTree() {
        TNULL = new RedBlackNode("");
        TNULL.color = 0;
        root = TNULL;
    }

    // Left rotate utility
    private void leftRotate(RedBlackNode x) {
        RedBlackNode y = x.right;
        x.right = y.left;
        if (y.left != TNULL) {
            y.left.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }
        y.left = x;
        x.parent = y;
    }

    // Right rotate utility
    private void rightRotate(RedBlackNode x) {
        RedBlackNode y = x.left;
        x.left = y.right;
        if (y.right != TNULL) {
            y.right.parent = x;
        }
        y.parent = x.parent;
        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }
        y.right = x;
        x.parent = y;
    }

    // Balance the tree after insert
    private void fixInsert(RedBlackNode k) {
        RedBlackNode u;
        while (k.parent.color == 1) {
            if (k.parent == k.parent.parent.right) {
                u = k.parent.parent.left;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.left) {
                        k = k.parent;
                        rightRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    leftRotate(k.parent.parent);
                }
            } else {
                u = k.parent.parent.right;
                if (u.color == 1) {
                    u.color = 0;
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    k = k.parent.parent;
                } else {
                    if (k == k.parent.right) {
                        k = k.parent;
                        leftRotate(k);
                    }
                    k.parent.color = 0;
                    k.parent.parent.color = 1;
                    rightRotate(k.parent.parent);
                }
            }
            if (k == root) {
                break;
            }
        }
        root.color = 0;
    }

    // Insert a book title into the Red-Black Tree
    public void insert(String title) {
        RedBlackNode node = new RedBlackNode(title);
        RedBlackNode y = null;
        RedBlackNode x = root;

        while (x != TNULL) {
            y = x;
            if (node.title.compareTo(x.title) < 0) {
                x = x.left;
            } else {
                x = x.right;
            }
        }

        node.parent = y;
        if (y == null) {
            root = node;
        } else if (node.title.compareTo(y.title) < 0) {
            y.left = node;
        } else {
            y.right = node;
        }

        node.left = TNULL;
        node.right = TNULL;
        node.color = 1;
        fixInsert(node);
    }

    // In-order traversal to get the list of books
    public ObservableList<String> inorder() {
        ObservableList<String> bookList = FXCollections.observableArrayList();
        inorderHelper(this.root, bookList);
        return bookList;
    }

    // Helper function for in-order traversal
    private void inorderHelper(RedBlackNode node, ObservableList<String> bookList) {
        if (node != TNULL) {
            inorderHelper(node.left, bookList);
            bookList.add(node.title);
            inorderHelper(node.right, bookList);
        }
    }

    // Delete a node from the Red-Black Tree (not required in this example, but can be added for book removal)
    public void delete(String title) {
        deleteNodeHelper(root, title);
    }

    private void deleteNodeHelper(RedBlackNode node, String title) {
        // This is a placeholder for deletion logic, as it's not required in this version.
    }
}

// Main Library Management System Class (JavaFX)
public class LibraryManagementSystem extends Application {

    private RedBlackTree libraryTree = new RedBlackTree();
    private ObservableList<String> books = FXCollections.observableArrayList();
    private ListView<String> bookListView = new ListView<>(books);

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Library Management System");

        // Input field for book titles
        TextField bookTitleInput = new TextField();
        bookTitleInput.setPromptText("Enter Book Title");

        // Buttons for actions
        Button addBookButton = new Button("Add Book");
        Button borrowBookButton = new Button("Borrow Book");
        Button returnBookButton = new Button("Return Book");

        // Book list display
        bookListView.setPrefHeight(200);

        // Adding a book
        addBookButton.setOnAction(e -> {
            String title = bookTitleInput.getText().trim();
            if (!title.isEmpty()) {
                libraryTree.insert(title);
                books.setAll(libraryTree.inorder());
                bookTitleInput.clear();
                showAlert("Success", "\"" + title + "\" has been added.");
            } else {
                showAlert("Error", "Book title cannot be empty.");
            }
        });

        // Borrowing a book
        borrowBookButton.setOnAction(e -> {
            String selectedBook = bookListView.getSelectionModel().getSelectedItem();
            if (selectedBook != null) {
                libraryTree.delete(selectedBook);
                books.setAll(libraryTree.inorder());
                showAlert("Success", "You borrowed \"" + selectedBook + "\".");
            } else {
                showAlert("Error", "Please select a book to borrow.");
            }
        });

        // Returning a book
        returnBookButton.setOnAction(e -> {
            String title = bookTitleInput.getText().trim();
            if (!title.isEmpty()) {
                libraryTree.insert(title);
                books.setAll(libraryTree.inorder());
                bookTitleInput.clear();
                showAlert("Success", "You returned: " + title);
            } else {
                showAlert("Error", "Book title cannot be empty.");
            }
        });

        // Layout setup
        GridPane inputGrid = new GridPane();
        inputGrid.setPadding(new Insets(10));
        inputGrid.setVgap(10);
        inputGrid.setHgap(10);
        inputGrid.add(new Label("Book Title:"), 0, 0);
        inputGrid.add(bookTitleInput, 1, 0);
        inputGrid.add(addBookButton, 2, 0);

        VBox layout = new VBox(10, inputGrid, bookListView, borrowBookButton, returnBookButton);
        layout.setPadding(new Insets(10));

        // Scene setup
        Scene scene = new Scene(layout, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Show alert message
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
