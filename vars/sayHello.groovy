import io.nextlayer.ci.UtilExample

def call(String name = "human") {
     UtilExample util = new UtilExample()
    String imageName =util.doSomething()
    echo "Something : ${imageName}"
    echo "Hello, ${name}."
}