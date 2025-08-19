import java.util.Random;
import java.util.concurrent.Semaphore;

// Classe que mantem as estatisticas
class WebStatsSem {
    private long totalAccess = 0;
    private int totalPurchases = 0;
    private int totalFailures = 0;
    private int totalNothing = 0;
    private int onlineUsers = 0;
    private Semaphore mutex = new Semaphore(1);

    // Usuario acessa o sistema
    public void access() {
        try {
            mutex.acquire();
            totalAccess++;
            onlineUsers++;
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Usuario realiza uma compra
    public void purchase() {
        try {
            mutex.acquire();
            totalPurchases++;
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Ocorreu uma falha
    public void failure() {
        try {
            mutex.acquire();
            totalFailures++;
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Usuario nem compra nem falha
    public void nothing() {
        try {
            mutex.acquire();
            totalNothing++;
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Usuario faz logout
    public void logout() {
        try {
            mutex.acquire();
            onlineUsers--;
            mutex.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Impressao das estatisticas atuais
    public void printStats() {
        System.out.println("========= Estatisticas do Sistema =========");
        System.out.println("Total de Acessos: " + totalAccess);
        System.out.println("Total de Compras: " + totalPurchases);
        System.out.println("Total de Falhas: " + totalFailures);
        System.out.println("Total de acessos sem compras ou falhas: " + totalNothing);
        System.out.println("Usuarios Online: " + onlineUsers);
        System.out.println("=======================================================");
    }
}

// Classe que simula acoes de um usuario no sistema
class UserSimulationSem implements Runnable {
    private WebStatsSem stats;
    private Random random;

    public UserSimulationSem(WebStatsSem stats) {
        this.stats = stats;
        this.random = new Random();
    }

    @Override
    public void run() {
        try {
            // Usuario acessa o sistema
            stats.access();

            // Simula tempo navegando
            Thread.sleep(random.nextInt(300));

            // Decide se faz compra, falha ou apenas navega
            int action = random.nextInt(3); // 0 = compra, 1 = falha, 2 = nada
            if (action == 0) {
                stats.purchase();
            } else if (action == 1) {
                stats.failure();
            } else {
                stats.nothing();
            }

            // Simula tempo antes de logout
            Thread.sleep(random.nextInt(200));

            // Usuario sai do sistema
            stats.logout();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

// Classe principal que executa a simulacao concorrente
public class WebStatsSemMain {
    public static void main(String[] args) {

        if (args.length < 1) {
            System.err.println("Usage: java WebStatsMain number_users");
            System.exit(1);
        }

        int numUsers = Integer.valueOf(args[0]); // quantidade de threads (usuarios simultaneos)

        WebStatsSem stats = new WebStatsSem();
        Thread[] users = new Thread[numUsers];

        // Criacao e inicializacao das threads
        for (int i = 0; i < numUsers; i++) {
            users[i] = new Thread(new UserSimulationSem(stats));
            users[i].start();
        }

        // Aguarda todas as threads terminarem
        for (int i = 0; i < numUsers; i++) {
            try {
                users[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Imprime estatisticas finais
        stats.printStats();
    }
}