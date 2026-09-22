package pl.edu.pwr.pwrinspace.poliwrocket.Model.Csp;

/**
 * Callback invoked when a CSP packet arrives addressed to a locally
 * registered destination port. Mirrors the role of the switch/case on
 * csp_conn_dport() inside csp_server_task() in csp_task.c.
 */
@FunctionalInterface
public interface CspPortHandler {
    void onPacket(CspPacket packet);
}
