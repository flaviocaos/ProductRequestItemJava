
package br.com.firsti.packages.stock.modules.productRequestItem.actions;

import static br.com.firsti.packages.stock.common.StockReferences.*;

import br.com.firsti.languages.LRCore;
import br.com.firsti.languages.LRProduct;
import br.com.firsti.languages.LRStock;
import br.com.firsti.module.actions.AbstractActionView;
import br.com.firsti.module.actions.AbstractActionView.WindowBuilder;
import br.com.firsti.module.exceptions.AccessDeniedException;
import br.com.firsti.module.exceptions.InternalServerErrorException;
import br.com.firsti.module.exceptions.PermissionDeniedException;
import br.com.firsti.module.exceptions.ResourceNotFoundException;
import br.com.firsti.module.requests.ActionRequest;
import br.com.firsti.packages.core.modules.resource.classes.ResourceManager;
import br.com.firsti.packages.core.modules.resource.classes.ResourceWrapper;
import br.com.firsti.packages.product.entities.Product;
import br.com.firsti.packages.product.entities.ProductAccessoryType;
import br.com.firsti.packages.stock.entities.ProductRequest;
import br.com.firsti.packages.stock.entities.ProductRequest.ProductRequestDestination;
import br.com.firsti.packages.stock.entities.ProductRequest.ProductRequestStatus;
import br.com.firsti.packages.stock.entities.ProductRequestItem;
import br.com.firsti.packages.stock.modules.productRequest.actions.ProductRequestViewWithdrawals;
import br.com.firsti.packages.stock.modules.productRequestItem.ModuleProductRequestItem;
import br.com.firsti.persistence.EntityManagerWrapper;
import br.com.firsti.services.websocket.messages.output.elements.DataFormat;
import br.com.firsti.services.websocket.messages.output.elements.ElementRequest;
import br.com.firsti.services.websocket.messages.output.elements.items.Button;
import br.com.firsti.services.websocket.messages.output.elements.items.Container;
import br.com.firsti.services.websocket.messages.output.elements.items.ElementGroup;
import br.com.firsti.services.websocket.messages.output.elements.items.InputView;
import br.com.firsti.services.websocket.messages.output.elements.items.PictureView;
import br.com.firsti.services.websocket.messages.output.elements.items.Textarea;

public class ProductRequestItemView extends AbstractActionView<ModuleProductRequestItem> {

    public ProductRequestItemView() {
        super(new Builder<>(Access.COMPANY_PUBLIC));
    }

    @Override
    public void onWindowRequest(EntityManagerWrapper entityManager, ActionRequest request, WindowBuilder windowBuilder)
            throws AccessDeniedException, PermissionDeniedException, ResourceNotFoundException,
            InternalServerErrorException {

        ProductRequestItem productRequestItem = entityManager.find(ProductRequestItem.class, request.getEntityId());

        if (productRequestItem == null) {
            throw new ResourceNotFoundException();
        }

        ProductRequest productRequest = productRequestItem.getProductRequest();

        if (!request.getUserProfile().isAdministrator()
                && !productRequest.isRequester(request.getUserProfile().getCollaborator())
                && !productRequest.getWarehouse().isResponsible(request.getUserProfile().getCollaborator())) {
            throw new AccessDeniedException();
        }

        configureTabs(productRequestItem, windowBuilder);

        windowBuilder.getTabsBuilder().add(ProductRequestItemViewWithdrawals.class);

        ResourceWrapper picture = null;

        if (productRequestItem.getProduct() != null) {
            picture = ResourceManager
                    .newInstance(entityManager, Product.class, productRequestItem.getProduct().getId(), PICTURE)
                    .getFirstResource();
        } else if (productRequestItem.getAccessoryType() != null) {
            picture = ResourceManager
                    .newInstance(entityManager, ProductAccessoryType.class, productRequestItem.getAccessoryType().getId(), PICTURE)
                    .getFirstResource();
        }

        if (picture != null) {
            windowBuilder.getDataBuilder().add(PICTURE, picture.getBase64());	
        }

        windowBuilder.getDataBuilder()
                .add(COMPANY_ORIGIN, productRequest.getWarehouse().getCompany())
                .add(WAREHOUSE_ORIGIN, productRequest.getWarehouse())
                .add(CREATION, productRequest.getCreation())
                .add(STATUS, productRequest.getStatus())
                .add(TYPE, productRequest.getDestination())
                .add(DIVISION, productRequest.getDestinationDivision())
                .add(COLLABORATOR, productRequest.getDestinationCollaborator())
                .add(ITEM_TYPE, productRequestItem.getType())
                .add(CATEGORY, productRequestItem.getProductType().getCategory())
                .add(PRODUCT_TYPE, productRequestItem.getProductType())
                .add(UNIT, productRequestItem.getProductType().getUnit())
                .add(REQUESTED_QUANTITY, productRequestItem.getRequestedQuantity())
                .add(APPROVED_QUANTITY, productRequestItem.getApprovedQuantity())
                .add(CANCELED_QUANTITY, productRequestItem.getCanceledQuantity())
                .add(AVAILABLE_QUANTITY, productRequestItem.getAvailableQuantity())
                .add(WITHDRAWN_QUANTITY, productRequestItem.getWithdrawnQuantity())
                .add(DESCRIPTION, productRequestItem.getDescription());

        if (productRequestItem.getProduct() != null) {
            windowBuilder.getDataBuilder()
                    .add(PRODUCT, productRequestItem.getProduct());
        } else if (productRequestItem.getAccessoryType() != null) {
            windowBuilder.getDataBuilder()
                    .add(PRODUCT, productRequestItem.getAccessoryType());
        }

        windowBuilder.getHeaderBuilder()
                .add(new ElementGroup(ORIGIN).setLabel(LRCore.ORIGIN).addClass("col-7")
                        .add(new InputView(COMPANY_ORIGIN).setLabel(LRCore.COMPANY).addClass("col-3"))
                        .add(new InputView(WAREHOUSE_ORIGIN).setLabel(LRStock.WAREHOUSE).addClass("col-9"))
                )
                .add(new ElementGroup(REQUEST).setLabel(LRStock.REQUEST).addClass("col-5")
                        .add(new InputView(CREATION).setLabel(LRCore.CREATION).setFormat(DataFormat.DATETIME).addClass("col-4"))
                        .add(new InputView(STATUS).setLabel(LRCore.STATUS).setColor(productRequest.getStatus().color).setTranslate(LRStock.class).addClass("col-8"))
                );

        windowBuilder.getBodyBuilder()
                .add(new PictureView(PICTURE).setLabel(LRCore.PICTURE).setSize(121))
                .add(new Container(CONTAINER).addClass("col")
                        .add(new ElementGroup(ITEM).setLabel(LRStock.ITEM).addClass("col-12")
                                .add(new InputView(ITEM_TYPE).setLabel(LRCore.TYPE).setTranslate(LRStock.class).addClass("col-2"))
                                .add(new InputView(CATEGORY).setLabel(LRCore.CATEGORY).addClass("col-2"))
                                .add(new InputView(PRODUCT_TYPE).setLabel(LRProduct.PRODUCT_TYPE).addClass("col-2"))
                                .add(new InputView(PRODUCT).setLabel(LRStock.PRODUCT_ACCESORY).addClass("col-5"))
                                .add(new InputView(UNIT).setLabel(LRCore.UNIT).setTranslate(LRCore.class).addClass("col-1"))
                                .add(new Textarea(DESCRIPTION).setLabel(LRCore.DESCRIPTION).setMinHeight(52))
                        )
                )
                .add(new ElementGroup(QUANTITY).setLabel(LRCore.QUANTITY).addClass("col-12")
                        .add(new InputView(REQUESTED_QUANTITY).setLabel(LRStock.REQUESTED).setFormat(DataFormat.DECIMAL).addClass("col"))
                        .add(new InputView(APPROVED_QUANTITY).setLabel(LRStock.APPROVED).setFormat(DataFormat.DECIMAL).addClass("col"))
                        .add(new InputView(CANCELED_QUANTITY).setLabel(LRStock.CANCELED).setFormat(DataFormat.DECIMAL).addClass("col"))
                        .add(new InputView(AVAILABLE_QUANTITY).setLabel(LRStock.AVAILABLE).setFormat(DataFormat.DECIMAL).addClass("col"))
                        .add(new InputView(WITHDRAWN_QUANTITY).setLabel(LRStock.WITHDRAWN).setFormat(DataFormat.DECIMAL).addClass("col"))
                );

        if (productRequest.isRequester(request.getUserProfile().getCollaborator())
                && productRequest.getStatus() == ProductRequestStatus.DRAFT) {
            windowBuilder.getFooterBuilder()
                    .add(new Button(EDIT, LRCore.EDIT).setOnClick(ElementRequest.createPopupRequest(ProductRequestItemEdit.class).setEntityId(request.getEntityId())))
                    .add(new Button(REMOVE, LRCore.REMOVE).setOnClick(ElementRequest.createAuxiliaryRequest(ProductRequestItemRemove.class).setEntityId(request.getEntityId())));
        }
    }

    private void configureTabs(ProductRequestItem productRequestItem, WindowBuilder windowBuilder) {
        ProductRequest productRequest = productRequestItem.getProductRequest();
        if (productRequest.getStatus() == ProductRequestStatus.PROCESSING
                || productRequest.getStatus() == ProductRequestStatus.PROCESSED) {

            // Additional tab configuration logic
        }
    }

}
