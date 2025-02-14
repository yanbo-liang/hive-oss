package cc.cc3c.hive.oss.sync;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class HiveSyncMonitor {
    @Autowired
    private HiveUploadListener hiveUploadListener;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReadyEvent() {
        try {
            FileAlterationMonitor uploadMonitor = new FileAlterationMonitor();
            FileAlterationObserver alibabaStandardObserver = new FileAlterationObserver(hiveUploadListener.getAlibabaStandardFolder(), (file) -> {
                return !file.getName().endsWith(".hive");
            });
            alibabaStandardObserver.addListener(hiveUploadListener);
            FileAlterationObserver alibabaAchieveObserver = new FileAlterationObserver(hiveUploadListener.getAlibabaAchieveFolder(), (file) -> {
                return !file.getName().endsWith(".hive");
            });
            alibabaAchieveObserver.addListener(hiveUploadListener);
            uploadMonitor.addObserver(alibabaStandardObserver);
            uploadMonitor.addObserver(alibabaAchieveObserver);
            uploadMonitor.start();
        } catch (Exception e) {
            log.error("failed to start upload monitor", e);
        }
    }
}
