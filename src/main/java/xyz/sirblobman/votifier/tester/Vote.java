package xyz.sirblobman.votifier.tester;

public final record Vote(String serviceName, String username, String address, long timestamp) {
    public Vote(String serviceName, String username, String address) {
        this(serviceName, username, address, System.currentTimeMillis());
    }
}
