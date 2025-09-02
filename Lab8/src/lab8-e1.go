package main

import (
	"fmt"
	"math/rand"
)

func main() {
	buffer := make(chan int)

	go produce(buffer)
	go consume(buffer)

	for {
	}
}

func produce(buffer chan int) {
	for {
		x := rand.Intn(100)
		buffer <- x
	}
}

func consume(buffer chan int) {
	for {
		x := <-buffer
		if x > 50 {
			fmt.Println(x)
		}
	}
}
