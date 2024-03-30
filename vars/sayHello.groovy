import io.nextlayer.ci.UtilExample

def call(String name = "human") {
     UtilExample util = new UtilExample()
    String imageName =util.doSomething()
    String helloJava=util.helloJava()
    echo "output : ${sh(script: util.helloJava(), returnStdout: true)}"
    echo "Something : ${imageName}"
    echo "Hello, ${name}."
   
}