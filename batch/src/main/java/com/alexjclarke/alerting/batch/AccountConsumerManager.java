package com.alexjclarke.alerting.batch;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class AccountConsumerManager {
    private volatile boolean shuttingDown = false;
    @Getter private final Map<String, SimpleMessageListenerContainer> consumers = new ConcurrentHashMap<>();
    private final Object lifecycleMonitor = new Object();

    void registerAndStart(final String queueName, final SimpleMessageListenerContainer newListenerContainer) {
        synchronized (this.lifecycleMonitor) {
            if (shuttingDown) {
                LOG.warn("Shutdown process is underway.  Not registering consumer for queue {}", queueName);
                return;
            }

            final SimpleMessageListenerContainer oldListenerContainer = consumers.get(queueName);
            if (oldListenerContainer != null) {
                oldListenerContainer.stop();
            }
            newListenerContainer.start();
            consumers.put(queueName, newListenerContainer);
            LOG.info("Registered a new consumer on queue {}", queueName);
        }
    }

    public void stopConsumers() {
        synchronized (this.lifecycleMonitor) {
            shuttingDown = true;
            LOG.info("Shutting down consumers on queues {}", consumers.keySet());
            consumers.entrySet().parallelStream().forEach(entry -> {
                LOG.info("Shutting down consumer on queue {}", entry.getKey());
                try {
                    entry.getValue().stop();
                } catch (final Exception e) {
                    LOG.error("Encountered error while stopping consumer on queue " + entry.getKey(), e);
                }
            });

            LOG.info("Finished shutting down all consumers");
        }
    }
}
