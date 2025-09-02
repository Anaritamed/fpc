package main

import (
	"fmt"
	"math/rand"
)

func main() {
	buffer := make(chan int)
	closed := make(chan int)
	join := make(chan int)

	go produce(buffer, closed, rand.Intn(10000))
	go produce(buffer, closed, rand.Intn(10000))
	go consume(buffer, join)

	<-closed
	<-closed
	close(buffer)
	<-join
}

func produce(buffer chan int, closed chan int, n int) {
	for i := 0; i < n; i++ {
		x := rand.Intn(100)
		buffer <- x
	}

	closed <- 0
}

func consume(buffer chan int, join chan int) {
	for x := range buffer {
		if x > 50 {
			fmt.Println(x)
		}
	}

	join <- 0
}
