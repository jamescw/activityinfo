package org.activityinfo.ui.full.client.importer;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.page.ChooseSourcePage;
import org.activityinfo.ui.full.client.importer.page.ColumnMappingPage;
import org.activityinfo.ui.full.client.importer.page.ValidationPage;
import org.activityinfo.ui.full.client.widget.FullScreenOverlay;

public class ImportPresenter<T> {

    private EventBus eventBus = GWT.create(SimpleEventBus.class);

    private ImportDialog dialogBox = new ImportDialog();
    private FullScreenOverlay overlay = new FullScreenOverlay();

    private ChooseSourcePage chooseSourcePage;
    private ColumnMappingPage<T> matchingPage;
    private ValidationPage<T> validationPage;

    private enum Step {
        CHOOSE_SOURCE,
        COLUMN_MATCHING,
        VALIDATION
    }

    private Step currentStep;

    private Importer<T> importer;

    private ResourceLocator dispatcher;

    public ImportPresenter(ResourceLocator dispatcher, FormTree formTree) {
        this.dispatcher = dispatcher;
        this.importer = new Importer<T>(formTree);

        chooseSourcePage = new ChooseSourcePage(eventBus);
        matchingPage = new ColumnMappingPage<T>(importer);
        validationPage = new ValidationPage<T>(importer);


        dialogBox.getNextButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                nextPage();
            }
        });

        dialogBox.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                overlay.hide();
            }
        });

        dialogBox.getFinishButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                submitData();
            }
        });
    }

    protected void submitData() {

        dialogBox.getFinishButton().setEnabled(false);
        dialogBox.setStatusText("Importing...");

//        BatchCommand batch = new BatchCommand();
//        for (DraftModel draftModel : importer.getDraftModels()) {
//            //batch.add(importer.getBinder().createCommand(draftModel));
//        }
//
//        dispatcher.execute(batch, new AsyncCallback<BatchResult>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                dialogBox.setStatusText("Import failed: " + caught.getMessage());
//            }
//
//            @Override
//            public void onSuccess(BatchResult result) {
//                overlay.hide();
//            }
//        });
    }

    public void show() {
        setStep(Step.CHOOSE_SOURCE);
        overlay.show(dialogBox);
    }

    private void setStep(Step step) {
        this.currentStep = step;
        switch (step) {
            case CHOOSE_SOURCE:
                dialogBox.setPage(chooseSourcePage);
                break;
            case COLUMN_MATCHING:
                importer.setSource(chooseSourcePage.getImportSource());
                matchingPage.refresh();
                dialogBox.setPage(matchingPage);
                break;
            case VALIDATION:
                importer.updateDrafts();
                validationPage.refresh();
                dialogBox.setPage(validationPage);
                break;
        }
        boolean lastPage = currentStep.ordinal() + 1 == Step.values().length;
        dialogBox.getNextButton().setVisible(!lastPage);
        dialogBox.getFinishButton().setVisible(lastPage);
    }

    private void nextPage() {
        setStep(Step.values()[currentStep.ordinal() + 1]);
    }
}