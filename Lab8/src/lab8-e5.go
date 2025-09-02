package main

import (
	"fmt"
	"math/rand"
)

func main() {
	buffer := make(chan int, 100)
	closed := make(chan int)
	join := make(chan int)

	go produce(buffer, closed, rand.Intn(10000))
	go produce(buffer, closed, rand.Intn(10000))

	for id := 1; id <= 5; id++ {
		go consume(buffer, join, id)
	}

	<-closed
	<-closed
	close(buffer)
	<-join
}

func produce(out chan<- int, closed chan int, n int) {
	for i := 0; i < n; i++ {
		x := rand.Intn(100)
		out <- x
	}

	closed <- 0
}

func consume(in <-chan int, join chan int, id int) {
	for x := range in {
		if x > 50 {
			fmt.Println("Consumidor ", id, " recebeu ", x)
		}
	}

	join <- 0
}
