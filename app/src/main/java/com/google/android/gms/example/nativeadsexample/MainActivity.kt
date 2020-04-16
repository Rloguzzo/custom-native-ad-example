/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.example.nativeadsexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.*
import kotlinx.android.synthetic.main.activity_main.*

const val AD_MANAGER_AD_UNIT_ID = "/6499/example/native"
const val SIMPLE_TEMPLATE_ID = "10104090"


/**
 * A simple activity class that displays native ad formats.
 */
class MainActivity : AppCompatActivity() {

    private var nativeCustomTemplateAd: NativeCustomTemplateAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refresh_button.setOnClickListener {
            refreshAd()
        }

        refreshAd()
    }

    /**
     * Populates a [View] object with data from a [NativeCustomTemplateAd]. This method
     * handles a particular "simple" custom native ad format.
     *
     * @param nativeCustomTemplateAd the object containing the ad's assets
     *
     * @param adView the view to be populated
     */
    private fun populateSimpleTemplateAdView(
            nativeCustomTemplateAd: NativeCustomTemplateAd,
            adView: View
    ) {
        val headlineView = adView.findViewById<TextView>(R.id.simplecustom_headline)
        val captionView = adView.findViewById<TextView>(R.id.simplecustom_caption)

        headlineView.text = nativeCustomTemplateAd.getText("Headline")
        captionView.text = nativeCustomTemplateAd.getText("Caption")

        val mediaPlaceholder = adView.findViewById<FrameLayout>(R.id.simplecustom_media_placeholder)
        val mainImage = ImageView(this)
        mainImage.adjustViewBounds = true
        mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").drawable)

        mainImage.setOnClickListener { nativeCustomTemplateAd.performClick("MainImage") }
        mediaPlaceholder.addView(mainImage)
        refresh_button.isEnabled = true
        videostatus_text.text = "Video status: Ad does not contain a video asset."
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestUnifiedNativeAds indicates whether unified native ads should be requested
     *
     * @param requestCustomTemplateAds indicates whether custom template ads should be requested
     */
    private fun refreshAd() {

        refresh_button.isEnabled = true
        nativeCustomTemplateAd?.destroy()

        val builder = AdLoader.Builder(this, AD_MANAGER_AD_UNIT_ID)

        builder.forCustomTemplateAd(SIMPLE_TEMPLATE_ID,
                { ad: NativeCustomTemplateAd ->
                    val frameLayout = findViewById<FrameLayout>(R.id.ad_frame)
                    val adView = layoutInflater
                            .inflate(R.layout.ad_simple_custom_template, null)
                    populateSimpleTemplateAdView(ad, adView)
                    frameLayout.removeAllViews()
                    frameLayout.addView(adView)
                    nativeCustomTemplateAd = ad
                },
                { ad: NativeCustomTemplateAd, s: String ->
                    Toast.makeText(this@MainActivity,
                            "A custom click has occurred in the simple template",
                            Toast.LENGTH_SHORT).show()
                })

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                refresh_button.isEnabled = true
                Toast.makeText(this@MainActivity, "Failed to load native ad: $errorCode",
                        Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(PublisherAdRequest.Builder().build())

        videostatus_text.text = ""
    }

    override fun onDestroy() {
        nativeCustomTemplateAd?.destroy()
        super.onDestroy()
    }
}
