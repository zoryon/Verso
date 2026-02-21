package it.zoryon.verso.core.utils.ytExtractor

import org.schabi.newpipe.extractor.NewPipe
import org.schabi.newpipe.extractor.downloader.Downloader

object ExtractorInitializer {
    fun init(downloader: Downloader) {
        NewPipe.init(downloader)
    }
}