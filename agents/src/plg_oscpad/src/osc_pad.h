
#ifndef __OSC_INPUT__
#define __OSC_INPUT__

#include "oscpad_plg_exports.h"

#include <piw/piw_bundle.h>
#include <piw/piw_data.h>
#include <piw/piw_clock.h>

namespace oscpad_plg
{

    class OSCPAD_PLG_DECLSPEC_CLASS osc_pad_t : public pic::nocopy_t
    {
        public:
    		osc_pad_t(piw::clockdomain_ctl_t *, const piw::cookie_t &output,
    				const std::string &send_host,
    				const std::string &send_port,
    				const std::string &recv_port);
            ~osc_pad_t();
            piw::cookie_t cookie();
            void set_window(unsigned top, unsigned height, unsigned left, unsigned width);
            void refresh();
            void play();
            void stop();
            void play_scene(int scene);
            void shutdown();
            void undo();
            void redo();

            class impl_t;
         private:
            impl_t *impl_;
    };
}

#endif

