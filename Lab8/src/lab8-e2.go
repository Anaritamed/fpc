package main

import (
	"fmt"
	"math/rand"
)

func main() {
	buffer := make(chan int)
	join := make(chan int)

	go produce(buffer)
	go consume(buffer, join)

	<-join
}

func produce(buffer chan int) {
	for i := 0; i < 10000; i++ {
		x := rand.Intn(100)
		buffer <- x
	}

	close(buffer)
}

func consume(buffer chan int, join chan int) {
	for x := range buffer {
		if x > 50 {
			fmt.Println(x)
		}
	}

	join <- 0
}
