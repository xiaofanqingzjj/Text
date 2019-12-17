/*
 * Copyright 2015 Google Inc.
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

package com.lgvalle.material_animations;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class for creating content transitions used with {@link android.app.ActivityOptions}.
 */
class TransitionHelper {

    /**
     * Create the transition participants required during a activity transition while
     * avoiding glitches with the system UI.
     *
     * @param activity The activity used as start for the transition.
     * @param includeStatusBar If false, the status bar will not be added as the transition
     *        participant.
     * @return All transition participants.
     */
    public static Pair<View, String>[] createSafeTransitionParticipants(@NonNull Activity activity, boolean includeStatusBar, @Nullable Pair<View, String>... otherParticipants) {

        // Avoid system UI glitches as described here:
        // https://plus.google.com/+AlexLockwood/posts/RPtwZ5nNebb
        View decor = activity.getWindow().getDecorView();

        View statusBar = null;
        if (includeStatusBar) {
            statusBar = decor.findViewById(android.R.id.statusBarBackground);
        }
        View navBar = decor.findViewById(android.R.id.navigationBarBackground);

        // Create pair of transition participants.
        List<Pair<View, String>> participants = new ArrayList<>(3);

        addNonNullViewToTransitionParticipants(statusBar, participants);
        addNonNullViewToTransitionParticipants(navBar, participants);

        // only add transition participants if there's at least one none-null element
        if (otherParticipants != null && !(otherParticipants.length == 1
                && otherParticipants[0] == null)) {
            participants.addAll(Arrays.asList(otherParticipants));
        }
        return participants.toArray(new Pair[participants.size()]);
    }

    private static void addNonNullViewToTransitionParticipants(View view, List<Pair<View, String>> participants) {
        if (view == null) {
            return;
        }
        participants.add(new Pair<View, String>(view, view.getTransitionName()));
    }




    private static void transitionToActivity(Activity activity, Class target) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, true);
        startActivity(activity, target, pairs, null);
    }


    private static void transitionToActivity(Activity activity, Class target, SamplesRecyclerAdapter.SamplesViewHolder viewHolder, Sample sample, int transitionName) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<View, String>(viewHolder.binding.sampleIcon, activity.getString(transitionName)));
        startActivity(activity, target, pairs, sample);
    }

    private  static void transitionToActivity(Activity activity, Class target, SamplesRecyclerAdapter.SamplesViewHolder viewHolder, Sample sample) {
        final Pair<View, String>[] pairs = TransitionHelper.createSafeTransitionParticipants(activity, false,
                new Pair<View, String>(viewHolder.binding.sampleIcon, activity.getString(R.string.square_blue_name)),
                new Pair<View, String>(viewHolder.binding.sampleName, activity.getString(R.string.sample_blue_title)));
        startActivity(activity, target, pairs, sample);
    }

    private static void startActivity(Activity activity, Class target, Pair<View, String>[] pairs, Sample sample) {

        Intent i = new Intent(activity, target);

        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, pairs);

        i.putExtra("sample", sample);
        activity.startActivity(i, transitionActivityOptions.toBundle());
    }


}
