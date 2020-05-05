package org.schabi.newpipe.extractor.services.bitchute;

import org.schabi.newpipe.extractor.StreamingService;
import org.schabi.newpipe.extractor.channel.ChannelExtractor;
import org.schabi.newpipe.extractor.comments.CommentsExtractor;
import org.schabi.newpipe.extractor.exceptions.ExtractionException;
import org.schabi.newpipe.extractor.kiosk.KioskExtractor;
import org.schabi.newpipe.extractor.kiosk.KioskList;
import org.schabi.newpipe.extractor.linkhandler.LinkHandler;
import org.schabi.newpipe.extractor.linkhandler.LinkHandlerFactory;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandler;
import org.schabi.newpipe.extractor.linkhandler.ListLinkHandlerFactory;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandler;
import org.schabi.newpipe.extractor.linkhandler.SearchQueryHandlerFactory;
import org.schabi.newpipe.extractor.playlist.PlaylistExtractor;
import org.schabi.newpipe.extractor.search.SearchExtractor;
import org.schabi.newpipe.extractor.services.bitchute.extractor.BitchuteChannelExtractor;
import org.schabi.newpipe.extractor.services.bitchute.extractor.BitchuteStreamExtractor;
import org.schabi.newpipe.extractor.services.bitchute.extractor.BitchuteTrendingTodayKioskExtractor;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteChannelLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteStreamLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.bitchute.linkHandler.BitchuteTrendingTodayKioskLinkHandlerFactory;
import org.schabi.newpipe.extractor.services.youtube.extractors.YoutubeSuggestionExtractor;
import org.schabi.newpipe.extractor.stream.StreamExtractor;
import org.schabi.newpipe.extractor.subscription.SubscriptionExtractor;
import org.schabi.newpipe.extractor.suggestion.SuggestionExtractor;


import static java.util.Arrays.asList;

public class BitchuteService extends StreamingService {

    public static final String BITCHUTE_LINK = "https://www.bitchute.com/";

    public BitchuteService(int id) {
        super(id, "BitChute", asList(ServiceInfo.MediaCapability.VIDEO));
    }

    @Override
    public String getBaseUrl() {
        return BITCHUTE_LINK;
    }

    @Override
    public LinkHandlerFactory getStreamLHFactory() {
        return BitchuteStreamLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getChannelLHFactory() {
        return BitchuteChannelLinkHandlerFactory.getInstance();
    }

    @Override
    public ListLinkHandlerFactory getPlaylistLHFactory() {
        return null;
    }

    @Override
    public SearchQueryHandlerFactory getSearchQHFactory() {
        //TODO
        return null;
    }

    @Override
    public ListLinkHandlerFactory getCommentsLHFactory() {
        return null;
    }

    @Override
    public SearchExtractor getSearchExtractor(SearchQueryHandler queryHandler) {
        //TODO
        return null;
    }

    @Override
    public SuggestionExtractor getSuggestionExtractor() {
        return new YoutubeSuggestionExtractor(this);
    }

    @Override
    public SubscriptionExtractor getSubscriptionExtractor() {
        return null;
    }

    @Override
    public KioskList getKioskList() throws ExtractionException {
        KioskList list = new KioskList(this);
        try {
            list.addKioskEntry(new KioskList.KioskExtractorFactory() {
                @Override
                public KioskExtractor createNewKiosk(StreamingService streamingService, String url
                        , String kioskId) throws ExtractionException {
                    return new BitchuteTrendingTodayKioskExtractor(
                            BitchuteService.this,
                            new BitchuteTrendingTodayKioskLinkHandlerFactory().fromUrl(url),
                            kioskId);
                }
            },new BitchuteTrendingTodayKioskLinkHandlerFactory(),"Trending Today");
            list.setDefaultKiosk("Trending Today");
        }
        catch (Exception e){
            throw new ExtractionException(e);
        }

        return list;
    }

    @Override
    public ChannelExtractor getChannelExtractor(ListLinkHandler linkHandler) throws ExtractionException {
        return new BitchuteChannelExtractor(this,linkHandler);
    }

    @Override
    public PlaylistExtractor getPlaylistExtractor(ListLinkHandler linkHandler) throws ExtractionException {
        return null;
    }

    @Override
    public StreamExtractor getStreamExtractor(LinkHandler linkHandler) throws ExtractionException {
        return new BitchuteStreamExtractor(this,linkHandler);
    }

    @Override
    public CommentsExtractor getCommentsExtractor(ListLinkHandler linkHandler) throws ExtractionException {
        return null;
    }
}
