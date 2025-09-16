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

func main() {
	println(exec(10))
}
