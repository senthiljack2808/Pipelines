import io.nextlayer.ci.UtilExample

def call(String name = "human") {
     UtilExample util = new UtilExample()
    String imageName =util.doSomething()
    String helloJava=util.helloJava()
    echo "App Class JAVA : ${helloJava} "
    echo "Something : ${imageName}"
    echo "Hello, ${name}."
   
}