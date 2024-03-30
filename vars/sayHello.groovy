import io.nextlayer.ci.UtilExample

def call(String name = "human") {
     UtilExample util = new UtilExample()
    String imageName =util.doSomething()
    String helloJava=util.helloJava()
    def output = sh(script: "Hello : ${util.helloJava()}")
    echo "output : ${output}"
    echo "Something : ${imageName}"
    echo "Hello, ${name}."
   
}