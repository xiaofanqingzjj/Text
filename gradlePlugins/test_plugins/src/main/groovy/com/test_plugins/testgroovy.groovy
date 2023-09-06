



void foo() {
    int a = 1
    def b = 2

    def c = [1, 2, 3, ];
    c.forEach( {
        println("$it")
    })

    foo2({
        print("sss:$it");
    })

    foo3 {

    }

    config {

    }
}

void foo2(def ss) {
    ss.call(");")
}

void foo3(Closure c) {
    c.call(1, 3, 4)
}

void config(Closure c) {
    c.call(this)
}