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

	count := 0
	for count < 400 {
		select {
		case x := <-prod1:
			count++
			println("Prod1:", x)
		case y := <-prod2:
			count++
			println("Prod2:", y)
		case <-time.After(500 * time.Millisecond):
			println("timeout")
		}
	}
}
