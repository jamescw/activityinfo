/*
 * All Sigmah code is released under the GNU General Public License v3
 * See COPYRIGHT.txt and LICENSE.txt.
 */

package org.sigmah.client.offline.sync;

import java.util.Date;

import org.sigmah.client.dispatch.AsyncMonitor;
import org.sigmah.client.dispatch.Dispatcher;
import org.sigmah.client.dispatch.remote.DirectDispatcher;
import org.sigmah.client.offline.command.LocalDispatcher;
import org.sigmah.client.offline.install.InstallSteps;
import org.sigmah.shared.command.Command;
import org.sigmah.shared.command.Ping;
import org.sigmah.shared.command.result.VoidResult;

import com.bedatadriven.rebar.appcache.client.AppCache;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Provider;

public class SynchronizerImpl implements Synchronizer {
    private static final int SYNC_INTERVAL = 30000;

    private final LocalDispatcher localDispatcher;
    private final Dispatcher remoteDispatcher;
    private final AppCacheSynchronizer appCacheSynchronizer;
    private final DownSynchronizer downSychronizer;
    private final UpdateSynchronizer updateSynchronizer;


    @Inject
    public SynchronizerImpl(
                       LocalDispatcher localDispatcher,
                       DirectDispatcher remoteDispatcher,
                       AppCacheSynchronizer appCache,
                       DownSynchronizer synchronizer,
                       UpdateSynchronizer updateSynchronizer) {
    	this.appCacheSynchronizer = appCache;
    	this.localDispatcher = localDispatcher;
        this.remoteDispatcher = remoteDispatcher;
        this.downSychronizer = synchronizer;
        this.updateSynchronizer = updateSynchronizer;
    }

    @Override
    public void install(final AsyncCallback<Void> callback) {
    	
    	appCacheSynchronizer.ensureUpToDate(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				downSychronizer.startFresh(callback);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
    }

    @Override
    public void goOffline(AsyncCallback<Void> callback) {
        callback.onSuccess(null);
    }

    @Override
    public void getLastSyncTime(AsyncCallback<java.util.Date> callback) {
        downSychronizer.getLastUpdateTime(callback);
    }

    @Override
    public void validateOfflineInstalled(final AsyncCallback<Void> callback) {
    	downSychronizer.getLastUpdateTime(new AsyncCallback<Date>() {
			
			@Override
			public void onSuccess(Date result) {
				if(result == null) {
					callback.onFailure(new RuntimeException("Never synchronized"));
				} else {
					callback.onSuccess(null);
				}
				
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
    }

    @Override
    public void synchronize(final AsyncCallback<Void> callback) {
    	appCacheSynchronizer.ensureUpToDate(new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				// send updates to server, then on success request 
		    	// updates from server
		    	updateSynchronizer.sync(new AsyncCallback<Void>() {
					
					@Override
					public void onSuccess(Void arg0) {
				    	downSychronizer.start(callback); 
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
			}
			
			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}
		});
    	
    }

    @Override
    public void goOnline(final AsyncCallback<Void> callback) {
    
    	updateSynchronizer.sync(new AsyncCallback<Void>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
			}

			@Override
			public void onSuccess(Void result) {
		    	verifyConnectivity(callback);
			}
		});    	
    }

	private void verifyConnectivity(final AsyncCallback<Void> callback) {
		// confirm that we can connect to the server
    	remoteDispatcher.execute(new Ping(), null, new AsyncCallback<VoidResult>() {

			@Override
			public void onFailure(Throwable caught) {
				callback.onFailure(caught);
				
			}

			@Override
			public void onSuccess(VoidResult result) {
				callback.onSuccess(null);
			}
		});
	}

	@Override
	public boolean canHandle(Command command) {
		return localDispatcher.canExecute(command);
	}

	@Override
	public void execute(Command command, AsyncMonitor monitor,
			AsyncCallback callback) {
		
		localDispatcher.execute(command, monitor, callback);
		
	}
}
