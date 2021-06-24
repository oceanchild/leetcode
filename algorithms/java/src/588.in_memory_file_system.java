// "static void main" must be defined in a public class.
public class Main {
    public static void main(String[] args) {
        FileSystem fs = new FileSystem();
        fs.mkdir("/a/b/c");
        fs.addContentToFile("/a/aa", "Hello World");
        fs.addContentToFile("/a/aa", "\nBye!");
        System.out.println(fs.readContentFromFile("/a/aa"));
    }
}

class Node {
    Map<String, Node> children;
    String name;
    boolean isFile;
    String content;

    public Node(String name, boolean isFile, String content) {
        this.name = name;
        this.isFile = isFile;
        this.content = content;
    }

    public void addChild(Node node) {
        if (children == null) {
            children = new TreeMap<String, Node>();
        }
        children.put(node.name, node);
    }

    public Node getChild(String name) {
        return children == null ? null : children.get(name);
    }
}

class FileSystem {
    Node root;

    public FileSystem() {
        this.root = new Node("/", false, null);
    }

    public List<String> ls(String path) {
        if (!isValidPath(path)) {
            return null;
        }
        List<String> pathTokens = Arrays.asList(path.split("/"));
        Node node = root;
        for (String pathToken : pathTokens) {
            if (pathToken.isEmpty()) {
                continue;
            }
            node = node.getChild(pathToken);
            if (node == null) {
                return null;
            }
        }
        if (node.isFile) {
            return Arrays.asList(node.name);
        }
        if (node.children == null) {
            return new ArrayList();
        }
        List<String> pathNodes = new ArrayList<String>();
        for (String childName : node.children.keySet()) {
            pathNodes.add(childName);
        }
        return pathNodes;
    }

    public void mkdir(String path) {
        if (!isValidPath(path)) {
            return;
        }
        List<String> pathTokens = Arrays.asList(path.split("/"));
        boolean isCreatePath = false;
        Node node = root;
        for (String pathToken : pathTokens) {
            if (pathToken.isEmpty()) {
                continue;
            }
            if (isCreatePath) {
                node.addChild(new Node(pathToken, false, null));
                node = node.getChild(pathToken);
            } else {
                if (node.getChild(pathToken) == null) {
                    isCreatePath = true;
                    node.addChild(new Node(pathToken, false, null));
                    node = node.getChild(pathToken);
                }
            }
        }
    }

    public void addContentToFile(String path, String content) {
        if (!isValidPath(path) || isPathDirectory(path)) {
            return;
        }
        List<String> pathTokens = Arrays.asList(path.split("/"));
        Node node = root;
        for (String pathToken : pathTokens) {
            if (pathToken.isEmpty()) {
                continue;
            }
            if (pathToken == pathTokens.get(pathTokens.size() - 1)) {
                if (node.getChild(pathToken) == null) {
                    node.addChild(new Node(pathToken, true, content));
                    return;
                } else {
                    node = node.getChild(pathToken);
                    node.content += content;
                }
            }
            node = node.getChild(pathToken);
            if (node == null) {
                return; // what to do if the file path does not exist?
            }
        }
    }

    public String readContentFromFile(String path) {
        if (!isValidPath(path) || isPathDirectory(path)) {
            return null;
        }
        List<String> pathTokens = Arrays.asList(path.split("/"));
        Node node = root;
        for (String pathToken : pathTokens) {
            if (pathToken.isEmpty()) {
                continue;
            }
            if (pathToken == pathTokens.get(pathTokens.size() - 1)) {
                node = node.getChild(pathToken);
                return node != null && node.isFile ? node.content : null;
            }
            node = node.getChild(pathToken);
            if (node == null) {
                return null;
            }
        }
        return null;
    }

    private boolean isValidPath(String path) {
        if (path != null && path.charAt(0) == '/') {
            return true;
        }
        return false;
    }

    private boolean isPathDirectory(String path) {
        return path.charAt(path.length() - 1) == '/';
    }
}
