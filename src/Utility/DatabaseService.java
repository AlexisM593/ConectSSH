package Utility;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class DatabaseService {

    private List<ResellerSSH> resellerSSHs = new ArrayList<>();
    private List<Item> serversGenerated = new ArrayList<>();

    public DatabaseService() {
        resellerSSHs.add(new ResellerSSH("user1", 10, 0.005));
        resellerSSHs.add(new ResellerSSH("user2", 5, null));
    }

    public List<ResellerSSH> queryResellerSSHByUserId(String userId) {
        return resellerSSHs.stream()
                .filter(r -> r.getId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Item> queryServersGeneratedByOwner(String ownerId) {
        return serversGenerated.stream()
                .filter(item -> ownerId.equals(item.getOwner()))
                .collect(Collectors.toList());
    }

    public void updateResellerSSH(ResellerSSH reseller) {
        for (int i = 0; i < resellerSSHs.size(); i++) {
            if (resellerSSHs.get(i).getId().equals(reseller.getId())) {
                resellerSSHs.set(i, reseller);
                return;
            }
        }
        resellerSSHs.add(reseller);
    }

    public void addServerGenerated(Item item) {
        serversGenerated.add(item);
    }
}