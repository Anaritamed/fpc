package main

import (
	"math/rand"
	"time"
)

func exec(maxSleepsMs int) int {
	sleepTime := rand.Intn(maxSleepsMs)
	time.Sleep(time.Duration(sleepTime) * time.Millisecond)
	return sleepTime
}

func aux(maxSleepsMs int, results chan<- int) {
	results <- exec(maxSleepsMs)
}

func startProducer(maxSleepsMs int) <-chan int {
	results := make(chan int)
	for i := 0; i < 200; i++ {
		go aux(maxSleepsMs, results)
	}
	return results
}

func main() {
	prod1 := startProducer(250)
	prod2 := startProducer(700)

	for i := 0; i < 400; i++ {
		select {
		case x := <-prod1:
			println("Prod1:", x)
		case y := <-prod2:
			println("Prod2:", y)
		}
	}
}
